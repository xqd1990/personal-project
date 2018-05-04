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
		//accuracy of name prediction is 79.70 in sample size of 1000
		//accuracy of text prediction is 57.45 in sample size of 1000
		//and in sample size of 1500 and 2000, the accuracy does not change significantly
		
		int name_error = 0;
		int name_mid = 0;
		int text_error = 0;
		int text_mid = 0;
		
		//train1000.csv means in this csv file there are 1000 records.
		//thus train2000.csv means there are 2000 records
		File file = new File("C:\\Git\\train2000.csv");
		FileReader reader = new FileReader(file);
		
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> records = parser.getRecords();
		int count = 1;
		for(;count<records.size();count++){
			CSVRecord record = records.get(count);
			double name_predict = Double.parseDouble(record.get(3));
			double text_predict = Double.parseDouble(record.get(1));
			String result = record.get(6);
			if((name_predict>0.5&&"female".equals(result))||(name_predict<0.5&&"male".equals(result)))
				name_error++;
			else if(name_predict==0.5)
				name_mid++;
			if((text_predict>0.5&&"female".equals(result))||(text_predict<0.5&&"male".equals(result)))
				text_error++;
			else if(text_predict==0.5)
				text_mid++;
		}
		System.out.println(name_error+" "+name_mid+" "+text_error+" "+text_mid);
	}
	
}
