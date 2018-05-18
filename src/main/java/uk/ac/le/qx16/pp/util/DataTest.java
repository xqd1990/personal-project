package uk.ac.le.qx16.pp.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

//this class is used to test the accuracy of prediction only by name or text

public class DataTest {
	
	public static void main(String[] args) throws IOException{
		
		//test the accuracy of name prediction and text prediction
		//accuracy of name prediction is 68.6% in sample size of 1000
		//accuracy of text prediction is 51.3% in sample size of 1000
		//and in sample size of 1500, 2000 or more, the accuracy does not change significantly
		
		//if name prediction is correct, this variable++
		int name_correct = 0;
		//if text prediction is correct, this variable++
		int text_correct = 0;
		
		//train1000.csv means in this csv file there are 1000 records.
		File file = new File("C:\\Git\\train1000.csv");
		FileReader reader = new FileReader(file);
		//read in every line
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> records = parser.getRecords();
		int count = 1;
		for(;count<records.size();count++){
			CSVRecord record = records.get(count);
			//column 3 is the result by name prediction
			double name_predict = Double.parseDouble(record.get(3));
			//column 1 is the result by text prediction
			double text_predict = Double.parseDouble(record.get(1));
			//column 6 is the labelled gender
			String result = record.get(6);
			//if name prediction is correct
			if((name_predict<0.5&&"female".equals(result))||(name_predict>0.5&&"male".equals(result)))
				name_correct++;
			//if text prediction is correct
			if((text_predict<0.5&&"female".equals(result))||(text_predict>0.5&&"male".equals(result)))
				text_correct++;
		}
		
		System.out.println(name_correct*1.0/count+"--"+text_correct*1.0/count);
	}
	
}
