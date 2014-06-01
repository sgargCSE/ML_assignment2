import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class LinearUnit {
	public static final int MPG = 7;
	
	
	public static void main(String[] args) throws IOException {
	   //System.out.println("HELLO");
		BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
		String line = null;
		ArrayList<double[]> data = new ArrayList<double[]>(100);
		double[] average = new double[MPG+1]; 
		double[] max = new double[MPG+1];
		while ((line = reader.readLine()) != null) {
			if (line.matches("^[0-9].*")){
				//System.out.println(line);
				String s = line;
				double[] a = new double[MPG+1];
				String[] split = s.split(",");
				//System.out.println(split.length);
				//System.out.println(split[3]);
				for (int i = 0;i<split.length;i++){
					try {
						a[i] = Double.parseDouble(split[i]);
						average[i] += a[i];
						if (a[i] > max[i]){
							max[i] = a[i];
						}
					} catch (NumberFormatException e){ 
						a[i] = -1;
					}
					//System.out.print(split[i] + " ");
				}
				//System.out.println();
				data.add(a);
			}
		}
		
		for (int i=0;i<average.length;i++){
			average[i] = average[i]/data.size();
			max[i] = max[i] - average[i];
//			System.out.println("AVG = " + average[i] + " ------ " + max[i]);
		}
		
		double[] var = new double[MPG+1];
		for (int i=0;i<data.size();i++){
			double[] sample = data.get(i);
			for (int j=0;j<sample.length;j++){
				var[j] += (sample[j]-average[j])*(sample[j]-average[j]);
			}
		}
		
		for (int i = 0;i<var.length;i++){
			var[i] = Math.sqrt(var[i]/(data.size()-1))*2;
			System.out.println(" ** "+ max[i] + " .... " + var[i]);
		}
//if (0==0) return;
double mm = 10000;
double mm2 = 0;
//for (double learning = 0.001; learning < .7; learning+= .001){
		double[] weights = new double[MPG+1];
		Random r = new Random(100);
		for (int i = 0;i<MPG+1;i++){
			weights[i] = r.nextDouble();
			weights[i] = 0;
			//System.out.printf("%.2f ", weights[i]);
		}
		
		//10-fold cross validation
		double MSE_PREV = 0;
		double learning = 0.497;
		ArrayList<double[]>  crossValidation = new ArrayList<double[]>();
		for (int i = 0;i < data.size(); i++){
		   if (i%10 == 0){
		      crossValidation.add(data.get(i));
		   }
		}
		
		data.removeAll(crossValidation);
		
		for (int i = 0; i < data.size(); i++) {
		   int correct = 0;
			double MSE = 0;
			double[] newWeights = new double[MPG+1];
			double[] predictions = new double[i+1];
			for (int w = 0; w<weights.length; w++){	
				double wupdate = 0;
				for (int j = 0; j<= i; j++){
					double[] sample = data.get(j);
					boolean cont = true;
					for (int jj = 0;jj<sample.length;jj++){
						if (sample[jj] == -1){
							cont = false;
						}
					}
					if (!cont){
						continue;
					}
					//for (int iii=0;iii<sample.length;iii++) System.out.print(sample[iii]+" ");
					//System.out.println(sample);
					if (w == 0){
						predictions[j] = sumProduct(weights, sample,average,max,var);
						DecimalFormat df = new DecimalFormat("#.##");
						System.out.println(df.format(predictions[j])+ " -> " + (int)sample[MPG]);
						if ((Math.abs(predictions[j] - sample[MPG])) < 1){
							correct++;
							System.out.println("GOT ONE RIGHT");
						}
						MSE += Math.abs(predictions[j] - sample[MPG]) ;
					}
					int pred =(int) Math.round(predictions[j]);
					if (w == 0){
						wupdate += (- sample[MPG] + pred) * 1;
					}else{
						wupdate += (- sample[MPG] + pred) * (sample[w-1] - average[w-1])/var[w-1];							
					}

				}
				if (MSE > MSE_PREV){
					newWeights[w] = weights[w] - learning * .5 * 1/(i+1) * wupdate;
					//learning *= 0.95;
				}else{
					newWeights[w] = weights[w] - learning * 1.05 * 1/(i+1) * wupdate;
					//learning *= 1.05;
				}
				newWeights[w] = weights[w] - learning * 1/(i+1) * wupdate;
				//System.out.printf("%.2f ", weights[w]);
			}
			weights = newWeights.clone();
			System.out.println("Weights of each attribute");
			for (int iii=0;iii<weights.length;iii++) System.out.print("Weight: "+iii+"           ");
			System.out.println("");
			for (int iii=0;iii<weights.length;iii++) System.out.print(weights[iii]+" ");
			System.out.println("\n------------------------");
			MSE = MSE/data.size();
			if (i == data.size()-1) {
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(2);
				
				double a = data.size();
				double perc = correct/a*100;
			System.out.println("RESULTS("+i+")\nCorrectly Classified = " + correct +" ("+df.format(perc)+"%)\nTotal Error = " + MSE);
			
			 JFreeChart chart = ChartFactory.createScatterPlot(
			            "Leanred Fuel Consumption", // chart title
			            "Data", // x axis label
			            "Mpg", // y axis label
			            createDataset(data, predictions),// data  ***-----PROBLEM------***
			            PlotOrientation.VERTICAL,
			            true, // include legend
			            true, // tooltips
			            false // urls
			            );

			        // create and display a frame...
			        ChartFrame frame = new ChartFrame("ML - Linear Unit", chart);
			        frame.pack();
			        frame.setVisible(true);
			
			}
			if (i == (data.size()-1)){
				if (MSE < mm){
					mm = MSE;
	//				mm2 = learning;
				}
			}
			MSE_PREV  = MSE;
		}
//}		
//System.out.println ( "MIN ERROR = " + mm + " -> " + mm2);
		//mpg = -1.5 * cylinders - 1 * accelleration + 0.75 * year + 1.0
		
		//cross validation
		double error = 0;
		for (int i = 0;i<crossValidation.size();i++){
			double[] sample = crossValidation.get(i);
			double pred = sumProduct(weights, sample, average,max, var);
			error += Math.abs(pred - sample[MPG]);
		}
		System.out.println("Validation set error = " + error/crossValidation.size());

       
		
		
		
		
	}

	private static XYDataset createDataset(ArrayList<double[]> data, double[] predictions) {
		Random r = new Random();
	    XYSeriesCollection result = new XYSeriesCollection();
	    XYSeries series = new XYSeries("Prediction");
	    for (int i = 0; i < data.size(); i++) {
	    	boolean missingData  = false;
	    	for (int jj = 0;jj<data.get(i).length;jj++){
				if (data.get(i)[jj] == -1){
					missingData  = true;
				} 
			}
	    	if (!missingData) {
	    		double y = predictions[i];
	    		series.add(i, y);
	    		//double y = data.get(i)[7];
	    	}
	    	
	        
	    }
	    
	    result.addSeries(series);
	    XYSeries series2 = new XYSeries("Class");
	    for (int i = 0; i < data.size(); i++) {
	    	boolean missingData = false;
	    	for (int jj = 0;jj<data.get(i).length;jj++){
				if (data.get(i)[jj] == -1){
					missingData  = true;
				}
			}
	    	if (!missingData) {
	    		double y = data.get(i)[MPG];
		    	//double y = predictions[i]+1.0;
		        series2.add(i, y);
	    	}
	       
	    }
	    
	    
	    result.addSeries(series2);
	    
	    return result;
	}
	
	public static double sumProduct(double[] w, double [] data, double average[], double max[], double var[]){
		double sum = w[0];
		for (int i = 1;i<MPG+1;i++){
			sum += w[i] * (data[i-1]-average[i-1])/var[i-1];
		}
		return sum;
	}
	
	public static double sumProduct(double[] w, double [] data, double average[], double max[]){
		double sum = w[0];
		
		for (int i = 1;i<8;i++){
			//System.out.println(sum+"+=" + w[i] + " * " + data[i-1]);
			sum += w[i] * (data[i-1]-average[i-1])/max[i-1];
		}
		//System.out.println("F:"+sum);
		return sum;
	}
	

	public static double sumProduct(double[] w, double [] data, double average[]){
		double sum = w[0];
		
		for (int i = 1;i<8;i++){
			//System.out.println(sum+"+=" + w[i] + " * " + data[i-1]);
			sum += w[i] * (data[i-1]/average[i-1]);
		}
		//System.out.println("F:"+sum);
		return sum;
	}
	
	public static double sumProduct(double[] w, double [] data){
		double sum = w[0];
		
		for (int i = 1;i<8;i++){
			//System.out.println(sum+"+=" + w[i] + " * " + data[i-1]);
			sum += w[i] * data[i-1];
		}
		//System.out.println("F:"+sum);
		return sum;
	}
}

