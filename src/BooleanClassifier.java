import java.util.ArrayList;
import java.util.Random;


public class BooleanClassifier {

   /**
    * @param args
    */
   public static void main(String[] args) {
      // TODO Auto-generated method stub
      ArrayList<int[]> data = createTestData();
      
      //training
      double[] weights = new double[data.get(0).length-1];
      int CLASS = data.get(0).length - 1;
      Random r = new Random();
      for (double w:weights){
    	  w = r.nextDouble();
      }
      double learning = 0.1;
      int correct = 0;
//      for (int[] sample:data){
//      for (int i =0; i < data.size();i++){
      for (int i =0; correct!=data.size();i++){
    	  int[] sample = data.get(i%data.size());
    	  double pred = 0;
    	  for (int j = 0;j<(sample.length-1);j++){
    		  pred += weights[j]*sample[j];
    	  }
    	  if (pred >= 0){
    		  pred = 1;
    	  }else{
    		  pred = -1;
    	  }
    	  for (int j = 0;j<(sample.length-1);j++){
    		  weights[j] += learning * (sample[CLASS] - pred) * sample[j];
    	  }
    	  if (sample[CLASS] == pred) {
    		  correct++;
    	  }else{
    		  System.out.println("c = "+correct);
    		  correct = 0;
    	  }
      }
      for (int j = 0; j < weights.length; j++){
    	  System.out.println(j+": " + weights[j]);
      }
      System.out.println("CORRECT = "+correct + "/"+data.size());
   }
   
   
   

   private static ArrayList<int[]> createTestData() {
      //creating test data
        ArrayList<int[]> data = new ArrayList<int[]>();
        
        for (int a=0;a<2;a++){
           for (int b=0;b<2;b++){
              for (int c=0;c<2;c++){
                 for (int d=0;d<2;d++){
                    for (int e=0;e<2;e++){
                       for (int f=0;f<2;f++){
                          for (int g=0;g<2;g++){
                             for (int h=0;h<2;h++){
                                int[] z = new int[8+1+1];
                                z[0] = 1;
                                z[1] = a;
                                z[2] = b;
                                z[3] = c;
                                z[4] = d;
                                z[5] = e;
                                z[6] = f;
                                z[7] = g;
                                z[8] = h;
                                
                                
                                

                                //LINEARLY SEPERABLE AND FUNCTION
                                //z[9] = (z[1] * z[2]) + z[3];
                                //z[9] = (~z[1])*z[2];
                                z[9] = z[1]*z[2]*z[3]*z[4]*z[5]*z[6]*z[7]*z[8];
                                
                                //non linearly seperable
                                //z[9] = z[1]*z[2] + z[7]*z[8];
                                
                                if (z[9] > 0) {
                                	z[9] = 1;
                                }else{
                                	z[9] = 0;
                                }
                                		//OR(AND(z[0],z[1]), z[2]);
                                data.add(z);
                             }
                          }
                       }
                    }
                 }
              }
           }
        }
        
        for (int i = 0; i < data.size(); i++){
           int[] test = data.get(i);
           for (int j = 0; j < test.length; j++){
        	   if (test[j] == 0) test[j] = -1;
              if (j == test.length-1) {
                 System.out.print(test[j]);
              } else {
                 System.out.print(test[j] +", ");
              }
           }
           System.out.println("");
        }
        return data;
   }

   private static int OR(int i, int j) {
      if (i == 1 ||j == 1) {
         return 1;
      }
      return 0;
      
      
   }

   private static int AND(int i, int j) {
   
      if (i == 1 && j == 1) {
         return 1;
      }
      return 0;
      
   }
}
