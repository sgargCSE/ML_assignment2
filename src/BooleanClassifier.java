import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;


public class BooleanClassifier {

   /**
    * @param args
    */
   public static void main(String[] args) {
      // TODO Auto-generated method stub
      ArrayList<int[]> data = createTestData();

      //Initilize the weights to small random values, between 0 - 1
      double[] weights = new double[data.get(0).length-1];
      int CLASS = data.get(0).length - 1;
      Random r = new Random();
      for (double w:weights){
         w = r.nextDouble();
      }
      
      //proven learning rate based on research
      double learning = 0.1;
      int correct = 0;

      //trains until all data is correctly predicted
      int counter = 0;
      //keep going till converged FULLY (standard practice for boolean classifiers
      for (int i =0; correct!=data.size();i++){
         //MOD so that i can keep on going forever and will just stream through
         //dataset as if it were reading an endless buffer of data
         int[] sample = data.get(i%data.size());
         double pred = 0;
         //perceptron
         for (int j = 0;j<(sample.length-1);j++){
            pred += weights[j]*sample[j];
         }
         
         //trigger points for classification
         if (pred >= 0){
            pred = 1;
         }else{
            pred = -1;
         }
         //weight updates
         for (int j = 0;j<(sample.length-1);j++){
            weights[j] += learning * (sample[CLASS] - pred) * sample[j];
         }
         
         //count of correct used for convergence criteria
         if (sample[CLASS] == pred) {
            correct++;
         }else{
            // Incorrect prediction, reset correct
            counter++;
            if (counter == 1000000){
               //trial end errored rate of giveup, runs for about 15 seconds,
               //which seems to be reasonable in finding all convergable functions
               System.out.println("------ DATASET NOT LINEARLY SEPERABLE -------\nGiving up now");
               System.out.println("BEST = " + correct);
               return;
            }
            correct = 0;
         }

      }
      
      //FINAL weights are printed
      System.out.println("Weights of each attribute");
      for (int j = 0; j < weights.length; j++){
         System.out.println("Weight "+j+": " + weights[j]);
      }
      DecimalFormat df = new DecimalFormat();
      df.setMaximumFractionDigits(2);
      System.out.println("CORRECT = "+correct + "/"+data.size()+"("+df.format(correct/data.size()*100)+"%)");
   }




   private static ArrayList<int[]> createTestData() {
      //creating test data
      ArrayList<int[]> data = new ArrayList<int[]>();

      //data generation 8 input, can be extended n by simply
      //adding another loop  in the middle section
      for (int a=0;a<2;a++){
         for (int b=0;b<2;b++){
            for (int c=0;c<2;c++){
               for (int d=0;d<2;d++){
                  for (int e=0;e<2;e++){
                     for (int f=0;f<2;f++){
                        for (int g=0;g<2;g++){
                           for (int h=0;h<2;h++){
                              int[] z = new int[8+1+1];
                              z[0] = 1; //bias weight
                              z[1] = a;
                              z[2] = b;
                              z[3] = c;
                              z[4] = d;
                              z[5] = e;
                              z[6] = f;
                              z[7] = g;
                              z[8] = h;

                              //Uses boolean algebra (and the NOT/XOR functions) to 
                              //represent all possible logic combinations of the inptus
                              //to test different functions simply replace the z[9] = 
                              //with required function, and it should all work nicely.
                              
                              //LINEARLY SEPERABLE FUNCTIONS
                              //z[9] = (z[1] * z[2]) + z[3];
                              //z[9] = (NOT(z[1]))*z[2];
                              z[9] = z[1]*z[2]*z[3]*z[4]*z[5]*z[6]*z[7]*z[8];

                              //NON - LINEARLY SEPERABLE FUNCTIONS
                              //z[9] = z[1]*z[2] + z[7]*z[8];

                              
                              if (z[9] > 0) {
                                 z[9] = 1;
                              }else{
                                 z[9] = -1;
                              }

                              data.add(z);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      //prints for good measure
      for (int i = 0; i < data.size(); i++){
         int[] test = data.get(i);
         for (int j = 0; j < test.length; j++){
            //if (test[j] == 0) test[j] = -1;
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
   
   private static int NOT(int i){
      if (i == 0){
         return 1;
      }else{
         return 0;
      }      
   }
   
   private static int XOR(int a,int b){
      if (a == 1 && b == 1)  return 0;
      if (a == 0 && b == 0)  return 0;
      return 1;      
   }

   /*
    * FUNCTIONS NOT NEEDED, easier prepresentations exist for these
    * 
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

   }*/
}
