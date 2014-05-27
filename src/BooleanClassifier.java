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
//      for (int i =0; i < data.size();i++){
      double learning = 0.1;
      int correct = 0;
      for (int[] sample:data){
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
    	  if (sample[CLASS] == pred) correct++;
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
                                int[] z = new int[8+1];
                                z[0] = a;
                                z[1] = b;
                                z[2] = c;
                                z[3] = d;
                                z[4] = e;
                                z[5] = f;
                                z[6] = g;
                                z[7] = h;
                                
                                z[8] = OR(AND(z[0],z[1]), z[2]);
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
