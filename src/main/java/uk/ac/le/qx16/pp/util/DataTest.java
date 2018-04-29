package uk.ac.le.qx16.pp.util;

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

public class DataTest {
	
	public static void main(String[] args) throws IOException{
		//test the accuracy of the third api (https://www.textgain.com/api)
		//testing:69% 
		//training:75%
		/*InputStream in = DataTest.class.getClassLoader().getResourceAsStream("training.csv");
		Reader reader = new InputStreamReader(in);
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> records = parser.getRecords();
		parser.close();
		reader.close();
		int correct = 0;
		for(int i=1;i<records.size();i++){
			CSVRecord record = records.get(i);
			if(record.get(7).startsWith(record.get(0))){
				correct++;
			}
		}
		System.out.println(correct*1.0/(records.size()-1));*/
		
		//preparing the training data for python program
		InputStream in = DataTest.class.getClassLoader().getResourceAsStream("testing.csv");
		Reader reader = new InputStreamReader(in);
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
		List<CSVRecord> records = parser.getRecords();
		parser.close();
		reader.close();
		FileWriter writer1 = new FileWriter("C:\\Git\\testingdata.csv");
		FileWriter writer2 = new FileWriter("C:\\Git\\testingresult.csv");
		CSVPrinter printer1 = new CSVPrinter(writer1, CSVFormat.EXCEL);
		CSVPrinter printer2 = new CSVPrinter(writer2, CSVFormat.EXCEL);
		for(int i=1;i<records.size();i++){
			CSVRecord record = records.get(i);
			if(record.get(0).equals("m")){
				printer1.printRecord(new String[]{record.get(1),record.get(2),record.get(3),record.get(4),record.get(5),record.get(6)});
			}else{
				printer1.printRecord(new String[]{"-"+record.get(1),record.get(2),record.get(3),record.get(4),record.get(5),record.get(6)});
			}
			if(record.get(7).equals("male")){
				printer2.printRecord(new String[]{"1","0"});
			}else{
				printer2.printRecord(new String[]{"0","1"});
			}
		}
		printer1.flush();
		printer1.close();
		writer1.close();
		printer2.flush();
		printer2.close();
		writer2.close();
	}
	
}
