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
		    		System.out.print(split[i] + " ");
		    	}
		    	System.out.println();
		    	data.add(a);
		    }
		}
		double[] weights = new double[8];
		Random r = new Random();
		for (int i = 0;i<8;i++){
			weights[i] = r.nextDouble();
		}
		
		
		
		for (double[] a : data) {
			double pred = sumProduct(weights, a);
			if (((int)pred) != a[0]){
				
				//update the weights
			}
		}
	}
	
	public static double sumProduct(double[] w, double [] data){
		double sum = w[0];
		for (int i = 1;i<data.length;i++){
			sum += w[i] * data[i];
		}
		return sum;
	}
}


