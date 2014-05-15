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
					} catch (NumberFormatException e){ 
						a[i] = -1;
					}
					//System.out.print(split[i] + " ");
				}
				//System.out.println();
				data.add(a);
			}
		}

		double[] weights = new double[8];
		Random r = new Random();
		for (int i = 0;i<8;i++){
			weights[i] = r.nextDouble();
			System.out.printf("%.2f ", weights[i]);
		}
		System.out.println();

		for (int i = 0; i < 3; i++) {
			double[] newWeights = new double[8];
			double[] predictions = new double[i+1];
			for (int w = 0; w<weights.length; w++){
				double wupdate = 0;
				for (int j = 0; j<= i; j++){
					double[] sample = data.get(j);
					for (int iii=0;iii<sample.length;iii++) System.out.print(sample[iii]+" ");
					//System.out.println(sample);
					if (j == 0){
						predictions[j] = sumProduct(weights, sample);
					}
					double pred = predictions[j];
					if (((int)pred) != sample[7]){
						//update the weights
						System.out.println((int)pred + " -> " + (int)sample[7]);
						if (w == 0){
							wupdate += (sample[7] - pred) * -1;
						}else{
							wupdate += (sample[7] - pred) * -sample[w-1];							
						}
					}else{
						//we predicted correctly
					}

				}
				newWeights[w] = weights[w] - 0.1 * wupdate;
				//System.out.printf("%.2f ", weights[w]);
			}
			weights = newWeights.clone();
			for (int iii=0;iii<weights.length;iii++) System.out.print(weights[iii]+" ");
			System.out.println("------------------------");
		}
	}

	public static double sumProduct(double[] w, double [] data){
		double sum = w[0];
		for (int i = 1;i<(data.length-1);i++){
			sum += w[i] * data[i-1];
		}
		return sum;
	}
}


