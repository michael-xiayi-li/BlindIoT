
public class HelloMyo {
	 public static void main(String[] args) {
		 Hub hub = new Hub("com.example.HelloMyo");
		 Myo myo = hub.waitForMyo(10000);  
	    }
}
