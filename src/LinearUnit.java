import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;


public class LinearUnit {
	public static final int MPG = 9;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//boolean perceptron
		//createTestData();
		
		//Read from dataset
		BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
		String line = null;
		ArrayList<double[]> data = new ArrayList<double[]>(100);
		double[] average = new double[10]; 
		double[] max = new double[10];
		while ((line = reader.readLine()) != null) {
			if (line.matches("^[0-9].*")){
				//System.out.println(line);
				String s = line;
				
				double[] b = new double[10];
				
				String[] split = s.split(",");
				
				//System.out.println(split.length);
				//System.out.println(split[3]);
				b[0]=1;
				for (int i = 0;i<(split.length-1);i++){
					try {
						b[i+1] = Double.parseDouble(split[i]);						
						average[i+1] += b[i+1];
						if (b[i+1] > max[i+1]){
							max[i+1] = b[i+1];
						}
					} catch (NumberFormatException e){ 
			
						b[i+1] = -1;
					}
					//System.out.print(split[i] + " ");
				}
				
				b[8] = b[3] * b[5]; //model * weight
				average[8] += b[8];
				if (b[8] > max[8]){
					max[8] = b[8];
				}

				b[9] = Double.parseDouble(split[split.length-1]);
				//System.out.println();
				data.add(b);
			}
		}
		
		//Compute variance for each variable
		for (int i=0;i<average.length;i++){
			average[i] = average[i]/data.size();
			max[i] = max[i] - average[i];
//			System.out.println("AVG = " + average[i] + " ------ " + max[i]);
		}
		
		double[] var = new double[10];
		for (int i=0;i<data.size();i++){
			double[] sample = data.get(i);
			for (int j=0;j<sample.length;j++){
				var[j] += (sample[j]-average[j])*(sample[j]-average[j]);
			}
		}
		
		for (int i = 0;i<var.length;i++){
			var[i] = Math.sqrt(var[i]/(data.size()-1))*2;
			System.out.println(" *"+i+"* "+ max[i] + " .... " + var[i]);
		}
		
		
//if (0==0) return;
		double mm = 10000;
		double mm2 = 0;
//for (double learning = 0.001; learning < .7; learning+= .001){
		

		//initialise weights to a small random number
		double[] weights = new double[9];
		Random r = new Random(100);
		for (int i = 0;i<weights.length;i++){
			weights[i] = r.nextDouble();
			weights[i] = 0;
			//System.out.printf("%.2f ", weights[i]);
		}
		
		//10-fold cross validation
		//System.out.println();
		double MSE_PREV = 0;
		double learning = 0.497;
		ArrayList<double[]>  crossValidation = new ArrayList<double[]>();
		for (int i = 0;i < data.size(); i++){
		   if (i%10 == 0){
		      crossValidation.add(data.get(i));
		   }
		}
		data.removeAll(crossValidation);
		
		//loop through data - perform gradient descent
		
		//data.size()
		for (int i = 0; i < data.size(); i++) {
		   int correct = 0;
			double MSE = 0;
			double[] newWeights = new double[9];
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
			for (int iii=0;iii<weights.length;iii++) System.out.print(weights[iii]+" ");
			System.out.println("\n------------------------");
			MSE = MSE/data.size();
			System.out.println("RESULTS("+i+") " + correct +"\n Total Error = " + MSE);
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
		
		double error = 0;
		for (int i = 0;i<crossValidation.size();i++){
			double[] sample = crossValidation.get(i);
			double pred = sumProduct(weights, sample, average,max, var);
			error += Math.abs(pred - sample[MPG]);
		}
		System.out.println("Validation set error = " + error/crossValidation.size());
	}

	public static double sumProduct(double[] w, double [] data, double average[], double max[], double var[]){
		double sum = 0;//w[0];
		for (int i = 1;i<9;i++){
			if (i!=0){
				sum += w[i] * (data[i-1]-average[i-1])/var[i-1];				
			}else{
				sum += w[i] * (data[i-1]);
			}
			//System.out.println(sum+"+=" + w[i] + " * " + data[i-1]);

		}
//		System.out.println("F:"+sum);
		
		return sum;
	}
	
/*	public static double sumProduct(double[] w, double [] data, double average[], double max[]){
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
	}*/
}


