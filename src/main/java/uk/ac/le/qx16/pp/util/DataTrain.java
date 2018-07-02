package uk.ac.le.qx16.pp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/*
 * This class is used to train the gender model.
 * Moreover, the app will use the GENDER_MODEL 
 * attribute of this class to predict the user gender.
 */

public class DataTrain {
	
	public static final Logistic GENDER_MODEL;
	public static final Instances DATASET;
	
	static{
		Instances instances = null;
		Logistic logistic = new Logistic();
		try {
			ArffLoader loader = new ArffLoader();
			InputStream in = DataTrain.class.getResourceAsStream("/train1000.arff");
			loader.setSource(in);
			instances = loader.getDataSet();
			instances.setClassIndex(4);
			logistic.buildClassifier(instances);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Training Error...");
		}
		GENDER_MODEL = logistic;
		DATASET = instances;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//below is to transform csv file into arff file. 
		
		/*InputStream in = DataTrain.class.getResourceAsStream("/train1000with12.csv");
		OutputStream out = new FileOutputStream("src/main/resources/train1000with12.arff");
		CSVLoader loader = new CSVLoader();
		loader.setSource(in);
		Instances instances = loader.getDataSet();
		ArffSaver saver = new ArffSaver();
		saver.setInstances(instances);
		saver.setDestination(out);
		saver.writeBatch();
		in.close();
		out.close();*/
		
		//below is to train a model and test the accuracy
		String train_path = "src/main/resources/train1000.arff";
		String test_path = "src/main/resources/test500.arff";
		//labelled index
		int train_class_index = 4;
		int test_class_index = 4;
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setFile(new File(train_path));
		Instances instances = arffLoader.getDataSet();
		instances.setClassIndex(train_class_index);
		int train_total = instances.numInstances();
		int train_right = 0;
		Logistic logistic = new Logistic();
		logistic.buildClassifier(instances);
		//this loop is to calculate the training accuracy
		for(int i=0;i<train_total;i++){
			Instance instance = instances.instance(i);
			if((logistic.distributionForInstance(instance)[0]>0.5&&instance.classValue()==0)
				||(logistic.distributionForInstance(instance)[0]<=0.5&&instance.classValue()==1))
				train_right++;
		}
		System.out.println("Training Accuracy is "+1.0*train_right/train_total);
		arffLoader = new ArffLoader();
		arffLoader.setFile(new File(test_path));
		instances = arffLoader.getDataSet();
		instances.setClassIndex(test_class_index);
		int total = instances.numInstances();
		int right = 0;
		//this loop is to calculate the testing accuracy
		for(int i=0;i<total;i++){
			Instance instance = instances.instance(i);
			if((logistic.distributionForInstance(instance)[0]>0.5&&instance.classValue()==0)
				||(logistic.distributionForInstance(instance)[0]<=0.5&&instance.classValue()==1))
				right++;
		}
		System.out.println("Testing Accuracy is "+1.0*right/total);
		//1234 1000
		//Training Accuracy is 0.8148148148148148
		//Testing Accuracy is 0.816
		//12 1000
		//Training Accuracy is 0.7098098098098098
		//Testing Accuracy is 0.712
		//34 1000
		//Training Accuracy is 0.6445445445445446
		//Testing Accuracy is 0.642
		//1234 100
		
	}

}
