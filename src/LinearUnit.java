import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class LinearUnit {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
		String line = null;
		ArrayList<double[]> data = new ArrayList<double[]>(100);
		double[] average = new double[8]; 
		double[] max = new double[8];
		while ((line = reader.readLine()) != null) {
			if (line.matches("^[0-9].*")){
				//System.out.println(line);
				String s = line;
				double[] a = new double[8];
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
		}

		double[] weights = new double[8];
		Random r = new Random(100);
		for (int i = 0;i<8;i++){
			weights[i] = r.nextDouble();
			weights[i] = 0;
			//System.out.printf("%.2f ", weights[i]);
		}
		//System.out.println();

		for (int i = 0; i < data.size(); i++) {
			int correct = 0;
			double MSE = 0;
			double[] newWeights = new double[8];
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
						predictions[j] = sumProduct(weights, sample,average,max);
						System.out.println(((int) Math.round(predictions[j])) + " -> " + (int)sample[7]);
						if ((Math.abs(predictions[j] - sample[7])) < 1){
							correct++;
							System.out.println("GOT ONE RIGHT");
						}
						MSE += Math.abs(predictions[j] - sample[7]);
					}
					int pred =(int) Math.round(predictions[j]);
					if (w == 0){
						wupdate += (- sample[7] + pred) * 1;
					}else{
						wupdate += (- sample[7] + pred) * (sample[w-1] - average[w-1])/max[w-1];							
					}

				}
				newWeights[w] = weights[w] - 0.09 * 1/(i+1) * wupdate;
				//System.out.printf("%.2f ", weights[w]);
			}
			weights = newWeights.clone();
			for (int iii=0;iii<weights.length;iii++) System.out.print(weights[iii]+" ");
			System.out.println("\n------------------------");
			System.out.println("RESULTS " + correct +"\n Total Error = " + MSE/data.size());
		}
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


