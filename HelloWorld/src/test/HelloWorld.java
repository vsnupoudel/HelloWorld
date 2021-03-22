package test;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException; 
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import weka.classifiers.functions.SMO;
import weka.filters.unsupervised.instance.RemovePercentage;


public class HelloWorld {
	
//	public HelloWorld(String fileDpath, String directoryPath, String skPath) {
//		
//	}
	
	
	
	// will copy a fresh copy of skeleton.arff into this folder
	static String fileDirectoryPath = "D:\\handwritten_digit_corpus_2\\handwritten_digit_corpus\\skeleton.arff";
	// Put all the images (.pngs) in this folder
	static String directoryPath = "D:\\handwritten_digit_corpus_2\\handwritten_digit_corpus";
	// put the readymade skeleton.arff file in this folder ( without data)
	static String skPath = "D:\\handwritten_digit_corpus_2\\handwritten_digit_corpus\\skeleton\\skeleton.arff";
	
	public static void main(String[] args) throws Exception {
		//Convert images to a arff file with pixel values
		// A skeleton arff file with column definition already present in skeleton folder
		System.out.println("File writing started...");
		writefile(fileDirectoryPath,skPath , directoryPath);
		System.out.println("File writing completed...");
		
	// Read in file and run classifier
		//weka.core.WekaPackageManager.loadPackages(true);
		System.out.println("Reading arff file...");
		FileReader readf = new FileReader(fileDirectoryPath); 
		Instances data = new Instances(readf);
		data.setClassIndex(data.numAttributes() - 1);	
		
		// Attempting train test split
		data.randomize(new Random(1));
		data.stratify(10);		
		// Create a train instance and append to it				
		Instances train = data.trainCV(10,1, new Random(1));
		
		for (int i = 2; i < 10 ; i++) {
			Instances inst = data.trainCV(10,i, new Random(1));
		    train.addAll(inst);
		}	
		train.setClassIndex(data.numAttributes() - 1);

	  Instances test = data.trainCV(10,10, new Random(1));
	  test.setClassIndex(data.numAttributes() - 1);
	  
		// Build on train, test on test set
		System.out.println("Building  model...");	
//		System.out.println(data.toString());
//		System.out.println(train.toString());
		SMO svm = new SMO();
		svm.buildClassifier(train);
		
		System.out.println("Evaluating  model...");	
		
		Evaluation eval = new Evaluation(train);
		eval.evaluateModel(svm, test);
			 
		 //print stats -- not required to calculate confusion mtx, weka does it!
		System.out.println("Printing metrics...");	
		 System.out.println(eval.toSummaryString());
		 System.out.println(eval.toMatrixString());
		 System.out.println(eval.toClassDetailsString());
	}
	
	public static void writefile(String fileDirectoryPath, String skPath, String directoryPath) 
			throws IOException {
		// copy the skeleton file
		File dired = new File(fileDirectoryPath);
		File skdir = new File(skPath);
		Files.copy( skdir.toPath() , dired.toPath() , StandardCopyOption.REPLACE_EXISTING);
		// for writing to a .csv
		BufferedWriter br = new BufferedWriter(
				new FileWriter(fileDirectoryPath, true) );
		StringBuilder sb = new StringBuilder();
		// File directory
		File dir = new File(directoryPath);
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		    if ( child.getName().contains(".png") ) {
				GetPixels getpixels = new GetPixels(child.getAbsolutePath());
				int[] pixels = getpixels.get_pixels();		
			
				// Append strings from array
				for (int element : pixels) {
				 sb.append(element);
				 sb.append(",");
				}
		// append the class to the end of the line
				sb.append(child.getAbsoluteFile().getName().substring(0, 1) );
				sb.append("\n");
				      
		    }		    
		    }	   
		  } 	
		  // write the whole file in the end
		br.write(sb.toString());
		br.close();	 
		
		
	}

}