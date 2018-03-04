
public class Runner {

	public static void main (String args[]) {
	OpenCVCamera t = new OpenCVCamera();
	t.takePicture();
	DetectLabelsExample dle = new DetectLabelsExample();
	dle.getResult();
	}
}
