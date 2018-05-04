package uk.ac.le.qx16.pp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class DataTrain {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*below is to transform csv file into arff file. 
		InputStream in = DataTrain.class.getResourceAsStream("/test500.csv");
		OutputStream out = new FileOutputStream("src/main/resources/test500.arff");
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
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setFile(new File("src/main/resources/train1000.arff"));
		Instances instances = arffLoader.getDataSet();
		instances.setClassIndex(4);
		Logistic logistic = new Logistic();
		logistic.buildClassifier(instances);
		arffLoader = new ArffLoader();
		arffLoader.setFile(new File("src/main/resources/test500.arff"));
		instances = arffLoader.getDataSet();
		instances.setClassIndex(4);
		int total = instances.numInstances();
		int right = 0;
		for(int i=0;i<total;i++){
			Instance instance = instances.instance(i);
			//System.out.println(i+". "+logistic.distributionForInstance(instance)[0]+", and the true value is "+instance.classValue());
			if((logistic.distributionForInstance(instance)[0]>0.5&&instance.classValue()==0)||(logistic.distributionForInstance(instance)[0]<=0.5&&instance.classValue()==1))
				right++;
		}
		System.out.println(right*1.0/total);
		//0.816
	}

}
