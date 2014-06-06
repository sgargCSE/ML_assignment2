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
      BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
      String line = null;
      ArrayList<double[]> data = new ArrayList<double[]>(100);
      double[] average = new double[MPG+1]; 
      double[] max = new double[MPG+1];

      while ((line = reader.readLine()) != null) {
         if (line.matches("^[0-9].*")){
            String s = line;
            double[] a = new double[MPG+1];
            String[] split = s.split(",");

            //reading in the values and replacing with -1 if ? is seen
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
            }
            data.add(a);
         }
      }

      //used for normalisation of the data, needs to compute the average of all features
      for (int i=0;i<average.length;i++){
         average[i] = average[i]/data.size();
         max[i] = max[i] - average[i];
         //			System.out.println("AVG = " + average[i] + " ------ " + max[i]);
      }

      //computing the variance of the sample size
      double[] var = new double[MPG+1];
      for (int i=0;i<data.size();i++){
         double[] sample = data.get(i);
         for (int j=0;j<sample.length;j++){
            var[j] += (sample[j]-average[j])*(sample[j]-average[j]);
         }
      }
      //working out stddeviation
      for (int i = 0;i<var.length;i++){
         //we used stdDev*2 since it covers 95% of the values, and ensures the majority
         //of them are within the -1 to 1 range.
         var[i] = Math.sqrt(var[i]/(data.size()-1))*2;
         System.out.println(" ** "+ max[i] + " .... " + var[i]);
      }

      /*this stuff was used for testing the learning rate to apply
double mm = 10000;
double mm2 = 0;
for (double learning = 0.001; learning < .7; learning+= .001){
       */
      
      //inits the weights to zero, initially randoms were trialled, however
      //Research indicated that initilizing to zeros was a better way to do gradient
      //descent
      double[] weights = new double[MPG+1];
      Random r = new Random(100);
      for (int i = 0;i<MPG+1;i++){
         weights[i] = r.nextDouble();
         weights[i] = 0;
         //System.out.printf("%.2f ", weights[i]);
      }

      //10-fold cross validation
      double MAE_PREV = 0;
      double learning = 0.497;
      ArrayList<double[]>  crossValidation = new ArrayList<double[]>();
      for (int i = 0;i < data.size(); i++){
         if (i%10 == 0){
            //for larger cross validation,  60% case described in the report
            //  if (i%10==0 || i%10==1 || i%10==2 || i%10==3 || i%10==4 || i%10==5)
            crossValidation.add(data.get(i));
         }
      }

      data.removeAll(crossValidation);

      for (int i = 0; i < data.size(); i++) {
         int correct = 0;
         double MAE = 0;
         double[] newWeights = new double[MPG+1];
         double[] predictions = new double[i+1];
         for (int w = 0; w<weights.length; w++){	
            double wupdate = 0;
            //a method similar to batch gradient descent, however 
            //uses the whole sample up to the next iteration value for
            //updates. Found to be the most effective gradient descent update
            //method.
            for (int j = 0; j<= i; j++){
               double[] sample = data.get(j);
               boolean cont = true;
               for (int jj = 0;jj<sample.length;jj++){
                  if (sample[jj] == -1){
                     cont = false;
                  }
               }
               if (!cont){
                  //skip over missing data
                  continue;
               }

               if (w == 0){
                  predictions[j] = sumProduct(weights, sample,average,max,var);
                  DecimalFormat df = new DecimalFormat("#.##");
                  System.out.println(df.format(predictions[j])+ " -> " + (int)sample[MPG]);
                  if ((Math.abs(predictions[j] - sample[MPG])) < 1){
                     correct++;
                     System.out.println("GOT ONE RIGHT");
                  }
                  MAE += Math.abs(predictions[j] - sample[MPG]) ;
               }

               int pred =(int) Math.round(predictions[j]);
               if (w == 0){
                  //bias weight
                  wupdate += (- sample[MPG] + pred) * 1;
               }else{
                  //all other weights
                  wupdate += (- sample[MPG] + pred) * (sample[w-1] - average[w-1])/var[w-1];							
               }

            }

            //if error is worse in this itteration increase or decrease the learning rate on updating weights
            if (MAE > MAE_PREV){
               newWeights[w] = weights[w] - learning * .5 * 1/(i+1) * wupdate;
               //learning *= 0.95;
            }else{
               newWeights[w] = weights[w] - learning * 1.05 * 1/(i+1) * wupdate;
               //learning *= 1.05;
            }
            //newWeights[w] = weights[w] - learning * 1/(i+1) * wupdate;
            //System.out.printf("%.2f ", weights[w]);
         }

         //print out debug stuff
         weights = newWeights.clone();
         System.out.println("\n------------------------");
         System.out.println("Weights of each attribute");
         for (int iii=0;iii<weights.length;iii++) System.out.print("Weight: "+iii+"           ");
         System.out.println("");
         for (int iii=0;iii<weights.length;iii++) System.out.print(weights[iii]+" ");
         System.out.println("\n------------------------");
         MAE = MAE/data.size();

         if (i == data.size()-1) {
            //for graphing the output
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            double a = data.size();
            double perc = correct/a*100;
            System.out.println("RESULTS("+i+")\nCorrectly Classified = " + correct +" ("+df.format(perc)+"%)\nTotal Error = " + MAE);

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
         MAE_PREV  = MAE;
      }

      //cross validation of the dataset
      double error = 0;
      for (int i = 0;i<crossValidation.size();i++){
         double[] sample = crossValidation.get(i);
         double pred = sumProduct(weights, sample, average,max, var);
         error += Math.abs(pred - sample[MPG]);
      }
      System.out.println("Validation set error = " + error/crossValidation.size());
   }

   
   //graphing utility functions
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

   /**
    * Normalises the data and then applies the weights taking into consideration the bias weight on x0 = 1
    * @param w
    * @param data
    * @param average
    * @param max
    * @param var
    * @return
    */
   public static double sumProduct(double[] w, double [] data, double average[], double max[], double var[]){
      double sum = w[0];
      for (int i = 1;i<MPG+1;i++){
         sum += w[i] * (data[i-1]-average[i-1])/var[i-1];
      }
      return sum;
   }
   
   
/*
 * 
 * Useless functions pertaining to different attempts that we made to get classification working



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
    */
}

