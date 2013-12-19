package at.ac.tuwien.cloud.generator.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class CSVConcatenate {
	
	static String path1 = "E:\\tuwien\\!MasterThesis\\JMETER_DATA\\2\\resources.csv"; //normalload1
	static String path2 = "E:\\tuwien\\!MasterThesis\\JMETER_DATA\\4\\resources.csv"; //normalload2
	static String path3 = "E:\\tuwien\\!MasterThesis\\JMETER_DATA\\5\\resources.csv"; //narrowpeak
	
	static String resultsPath = "E:\\tuwien\\!MasterThesis\\workspace\\DataGenerator\\results\\";
	
	static ArrayList<ArrayList<Double>> normal1 = new ArrayList<ArrayList<Double>>();
	static ArrayList<ArrayList<Double>> normal2 = new ArrayList<ArrayList<Double>>();
	static ArrayList<ArrayList<Double>> narrowpeak = new ArrayList<ArrayList<Double>>();

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Started");
		readFile(path1,normal1);
		System.out.println("1. File Read");
		readFile(path2,normal2);
		System.out.println("2. File Read");
		readFile(path3,narrowpeak);
		System.out.println("3. File Read");
		
//		printArrayList(normal2);
		
		//Create perweek mixes ()
		
		ArrayList<ArrayList<Double>> mix0 = createNormalMix(normal1,14,false,1.0); //noo weekend rescaling
		
		ArrayList<ArrayList<Double>> mix1 = createNormalMix(normal1,14,true,0.4); // concat. of #1 sample with weekend scaling
		ArrayList<ArrayList<Double>> mix2 = createNormalMix(normal2,14,true,0.4); // concat. of #2 sample with weekend scaling
		ArrayList<ArrayList<Double>> mix3 = createNormalMix(narrowpeak,14,true,0.4); // concat. of #3 sample with weekend scaling
		
		ArrayList<ArrayList<Double>> mix4 = createMixedMix(normal2,narrowpeak,14,true,0.4,false); // concat. of #2 sample - #3 sample with weekend scaling
		ArrayList<ArrayList<Double>> mix5 = createMixedMix(normal2,narrowpeak,14,true,0.4,true); // concat. randomly of #2 sample - #3 sample with weekend scaling
		
		ArrayList<ArrayList<Double>> mix6 = createNormalMixWithPerDayScaling(normal2,14,0.3,true,0.4); // concat. of #1 sample with weekend scaling + per daily rescaling
		ArrayList<ArrayList<Double>> mix7 = createMixedMixWithPerDayScaling(normal1,narrowpeak,14,true,0.3,true,0.4); // concat. randomly of #1 sample - #3 sample with weekend scaling + per daily rescaling
		
//		printArrayList(normal1);
//		printArrayList(mix2);
		
		System.out.println("Concatenation finished.");
		System.out.println("Mix0 size: "+ mix0.get(0).size());
		System.out.println("Mix1 size: "+ mix1.get(0).size());
		System.out.println("Mix2 size: "+ mix2.get(0).size());
		System.out.println("Mix3 size: "+ mix3.get(0).size());
		System.out.println("Mix4 size: "+ mix4.get(0).size());
		System.out.println("Mix5 size: "+ mix5.get(0).size());
		System.out.println("Mix6 size: "+ mix6.get(0).size());
		System.out.println("Mix7 size: "+ mix7.get(0).size());
		
		manipulateAndSaveMix(mix0,"mix0");
		mix0.clear();
		manipulateAndSaveMix(mix1,"mix1");
		mix1.clear();
		manipulateAndSaveMix(mix2,"mix2");
		mix2.clear();
		manipulateAndSaveMix(mix3,"mix3");
		mix3.clear();
		manipulateAndSaveMix(mix4,"mix4");
		mix4.clear();
		manipulateAndSaveMix(mix5,"mix5");
		mix5.clear();
		manipulateAndSaveMix(mix6,"mix6");
		mix6.clear();
		manipulateAndSaveMix(mix7,"mix7");
		mix7.clear();
		//
		
//		saveCSV(mix0,"samples2",0);
//		saveCSV(mix1,"samples2",1);
//		saveCSV(mix2,"samples2",2);
//		saveCSV(mix3,"samples2",3);
//		saveCSV(mix4,"samples2",4);
//		saveCSV(mix5,"samples2",5);
//		saveCSV(mix6,"samples2",6);
//		saveCSV(mix7,"samples2",7);
		
		
	}

	private static void manipulateAndSaveMix(ArrayList<ArrayList<Double>> mix,	String path) {
		

		for (int i = 0; i < 200; i++) {
			ArrayList<ArrayList<Double>> returnliste = new ArrayList<ArrayList<Double>>();
			ArrayList<Double> temp1 = new ArrayList<Double>();
			ArrayList<Double> temp2 = new ArrayList<Double>();
			ArrayList<Double> temp3 = new ArrayList<Double>();
			ArrayList<Double> temp4 = new ArrayList<Double>();
			int randomscale = randInt(30, 130);
			double scalefactor = (double) randomscale / (double) 100;
			//scale by random 0.3 - 1.3
			for(int j = 0; j < mix.get(0).size();j++){
				temp1.add(mix.get(0).get(j)*scalefactor);
				temp2.add(mix.get(1).get(j)*scalefactor);
				temp3.add(mix.get(2).get(j)*scalefactor);
				temp4.add(mix.get(3).get(j)*scalefactor);
			}
			
			//shift by random 0-12 h = 0-720 datapoint
			int horizontalshift = randInt(0, 720);
			ArrayList<Double> shift1 = new ArrayList<Double>();
			ArrayList<Double> shift2 = new ArrayList<Double>();
			ArrayList<Double> shift3 = new ArrayList<Double>();
			ArrayList<Double> shift4 = new ArrayList<Double>();
			for(int j = 0; j < horizontalshift; j++){
				shift1.add(temp1.remove(j));
				shift2.add(temp2.remove(j));
				shift3.add(temp3.remove(j));
				shift4.add(temp4.remove(j));
			}
			temp1.addAll(shift1);
			temp2.addAll(shift2);
			temp3.addAll(shift3);
			temp4.addAll(shift4);
						
			
			returnliste.add(temp1);
			returnliste.add(temp2);
			returnliste.add(temp3);
			returnliste.add(temp4);
			saveCSV(returnliste,path,i);
			
			System.out.println(i+". File written. +");
			temp1.clear();
			temp2.clear();
			temp3.clear();
			temp4.clear();
			returnliste.clear();
		}
		
		
	}

	private static void saveCSV(ArrayList<ArrayList<Double>> mix, String path,int filenum) {
		
		File file = new File(resultsPath+path+File.separator+filenum+".csv");
		ArrayList<Double> temp1,temp2,temp3,temp4;
		temp1 = mix.get(0);
		temp2 = mix.get(1);
		temp3 = mix.get(2);
		temp4 = mix.get(3);
		
		BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("TIMESTAMP;" + "CPU;" + "MEM;" + "DISK;"+ "NET");
            writer.newLine();
    		for(int i = 0; i < temp1.size(); i++){
    			writer.write(i+1+";"+temp1.get(i)+";"+temp2.get(i)+";"+temp3.get(i)+";"+temp4.get(i));
    			writer.newLine();
    		}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	}

	private static ArrayList<ArrayList<Double>> createMixedMixWithPerDayScaling(
			ArrayList<ArrayList<Double>> liste1,
			ArrayList<ArrayList<Double>> liste2, int numdays, boolean randomized, double perdayvariance,
			boolean weekendscaling, double weekendscalefactor) {
		ArrayList<ArrayList<Double>> returnliste = new ArrayList<ArrayList<Double>>();


		ArrayList<Double> temp1 = new ArrayList<Double>();
		ArrayList<Double> temp2 = new ArrayList<Double>();
		ArrayList<Double> temp3 = new ArrayList<Double>();
		ArrayList<Double> temp4 = new ArrayList<Double>();

		ArrayList<ArrayList<Double>> liste = new ArrayList<ArrayList<Double>>();
		
		for(int i = 1; i <= numdays; i++ ){

			int random = randInt(70,100); // todo 70 = perdayvariance
			double perdayscale = (double) random / (double) 100;

			if(randomized){
				//randomly choose liste1/liste2
				if(Math.random() > 0.5){
					liste.clear();
					liste.addAll(liste1);
				}else{
					liste.clear();
					liste.addAll(liste2);
				}

			}else{
				//felvätva choose liste1/liste2
				if(i % 2 == 0){
					liste.clear();
					liste.addAll(liste1);
				}else{
					liste.clear();
					liste.addAll(liste2);
				}
			}


			for(int j = 0; j < liste.get(0).size();j++){
				if(i % 6 == 0 || i % 7 == 0 && weekendscaling == true){
					temp1.add((liste.get(0).get(j)*weekendscalefactor)*perdayscale);
					temp2.add((liste.get(1).get(j)*weekendscalefactor)*perdayscale);
					temp3.add((liste.get(2).get(j)*weekendscalefactor)*perdayscale);
					temp4.add((liste.get(3).get(j)*weekendscalefactor)*perdayscale);
				}else{

					temp1.add(liste.get(0).get(j)*perdayscale);
					temp2.add(liste.get(1).get(j)*perdayscale);
					temp3.add(liste.get(2).get(j)*perdayscale);
					temp4.add(liste.get(3).get(j)*perdayscale);
				}
			}
		}

		returnliste.add(temp1);
		returnliste.add(temp2);
		returnliste.add(temp3);
		returnliste.add(temp4);

		return returnliste;
	}

	private static ArrayList<ArrayList<Double>> createNormalMixWithPerDayScaling(
			ArrayList<ArrayList<Double>> liste, int numdays, double perdayscalevariance, boolean weekendscaling,
			double weekendscalefactor) {
		
		ArrayList<ArrayList<Double>> returnliste = new ArrayList<ArrayList<Double>>();


		ArrayList<Double> temp1 = new ArrayList<Double>();
		ArrayList<Double> temp2 = new ArrayList<Double>();
		ArrayList<Double> temp3 = new ArrayList<Double>();
		ArrayList<Double> temp4 = new ArrayList<Double>();

		for(int i = 1; i <= numdays; i++ ){

			int random = randInt(70,100); // todo 70 = perdayvariance
			double perdayscale = (double) random / (double) 100;


			for(int j = 0; j < liste.get(0).size();j++){
				if(i % 6 == 0 || i % 7 == 0 && weekendscaling == true){
					temp1.add((liste.get(0).get(j)*weekendscalefactor)*perdayscale);
					temp2.add((liste.get(1).get(j)*weekendscalefactor)*perdayscale);
					temp3.add((liste.get(2).get(j)*weekendscalefactor)*perdayscale);
					temp4.add((liste.get(3).get(j)*weekendscalefactor)*perdayscale);
				}else{

					temp1.add(liste.get(0).get(j)*perdayscale);
					temp2.add(liste.get(1).get(j)*perdayscale);
					temp3.add(liste.get(2).get(j)*perdayscale);
					temp4.add(liste.get(3).get(j)*perdayscale);
				}
			}
		}

		returnliste.add(temp1);
		returnliste.add(temp2);
		returnliste.add(temp3);
		returnliste.add(temp4);

		return returnliste;
	}

	private static ArrayList<ArrayList<Double>> createMixedMix(
			ArrayList<ArrayList<Double>> liste1,
			ArrayList<ArrayList<Double>> liste2,
			int numofdays,
			boolean weekendscaling,
			double scalefactor,
			boolean randomized) {

		ArrayList<ArrayList<Double>> returnliste = new ArrayList<ArrayList<Double>>();

		ArrayList<Double> temp1 = new ArrayList<Double>();
		ArrayList<Double> temp2 = new ArrayList<Double>();
		ArrayList<Double> temp3 = new ArrayList<Double>();
		ArrayList<Double> temp4 = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> liste = new ArrayList<ArrayList<Double>>();
		for(int i = 1; i <= numofdays; i++ ){

			if(randomized){
				//randomly choose liste1/liste2
				if(Math.random() > 0.5){
					liste.clear();
					liste.addAll(liste1);
				}else{
					liste.clear();
					liste.addAll(liste2);
				}

			}else{
				//felvätva choose liste1/liste2
				if(i % 2 == 1){
					liste.clear();
					liste.addAll(liste1);
				}else{
					liste.clear();
					liste.addAll(liste2);
				}
			}



			if(i % 6 == 0 || i % 7 == 0 && weekendscaling == true){

				for(int j = 0; j < liste.get(0).size();j++){
					temp1.add(liste.get(0).get(j)*scalefactor);
					temp2.add(liste.get(1).get(j)*scalefactor);
					temp3.add(liste.get(2).get(j)*scalefactor);
					temp4.add(liste.get(3).get(j)*scalefactor);
				}

			}else{

				temp1.addAll(liste.get(0));
				temp2.addAll(liste.get(1));
				temp3.addAll(liste.get(2));
				temp4.addAll(liste.get(3));

			}
		}

		returnliste.add(temp1);
		returnliste.add(temp2);
		returnliste.add(temp3);
		returnliste.add(temp4);

		return returnliste;
	}

	private static ArrayList<ArrayList<Double>> createNormalMix(ArrayList<ArrayList<Double>> liste,int numdays,boolean weekendscaling,double scalefactor) {

		ArrayList<ArrayList<Double>> returnliste = new ArrayList<ArrayList<Double>>();


		ArrayList<Double> temp1 = new ArrayList<Double>();
		ArrayList<Double> temp2 = new ArrayList<Double>();
		ArrayList<Double> temp3 = new ArrayList<Double>();
		ArrayList<Double> temp4 = new ArrayList<Double>();

		for(int i = 1; i <= numdays; i++ ){

			if(i % 6 == 0 || i % 7 == 0 && weekendscaling == true){

				for(int j = 0; j < liste.get(0).size();j++){
					temp1.add(liste.get(0).get(j)*scalefactor);
					temp2.add(liste.get(1).get(j)*scalefactor);
					temp3.add(liste.get(2).get(j)*scalefactor);
					temp4.add(liste.get(3).get(j)*scalefactor);
				}
				
			}else{

				temp1.addAll(liste.get(0));
				temp2.addAll(liste.get(1));
				temp3.addAll(liste.get(2));
				temp4.addAll(liste.get(3));

			}
		}
		
		returnliste.add(temp1);
		returnliste.add(temp2);
		returnliste.add(temp3);
		returnliste.add(temp4);
		
		return returnliste;
	}

	private static void printArrayList(ArrayList<ArrayList<Double>> liste) {
		ArrayList<Double> temp1,temp2,temp3,temp4;

		temp1 = liste.get(0);
		temp2 = liste.get(1);
		temp3 = liste.get(2);
		temp4 = liste.get(3);
		System.out.println("\tCPU\t\t" + "MEM\t\t" + "DISK\t\t"+ "NET\t\t");
		for(int i = 0; i < temp1.size(); i++){
			System.out.println(i+1+"#\t"+temp1.get(i)+"\t"+temp2.get(i)+"\t"+temp3.get(i)+"\t"+temp4.get(i)+"\t");
		}
		
	}

	private static void readFile(String csvfilepath, ArrayList<ArrayList<Double>> storeage) {
		
		 BufferedReader CSVFile = null;
		 String dataRow = null;
		 ArrayList<Double> datatempCPU = new ArrayList<Double>(); 
		 ArrayList<Double> datatempMEM = new ArrayList<Double>(); 
		 ArrayList<Double> datatempDISK = new ArrayList<Double>(); 
		 ArrayList<Double> datatempNET = new ArrayList<Double>(); 

		 try {
			 CSVFile = new BufferedReader(new FileReader(csvfilepath));
		 } catch (FileNotFoundException e) {
			 e.printStackTrace();
		 }
		 
		 try {
			 dataRow = CSVFile.readLine();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 
		 while (dataRow != null){
			 String[] dataArray = dataRow.split(";");
						 
			 if(dataArray[0].equals("Elapsed time")) {
				 try {
					 dataRow = CSVFile.readLine();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
				 continue;
			 }
//			System.out.println(dataRow);
			 datatempCPU.add(Double.valueOf(dataArray[1]));
			 datatempDISK.add(Double.valueOf(dataArray[2]));
			 datatempMEM.add(Double.valueOf(dataArray[3]));
			 datatempNET.add(Double.valueOf(dataArray[4]));
			 System.out.print(".");
			 //read next line
			 try {
				 dataRow = CSVFile.readLine();
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }

		 storeage.add(new ArrayList<Double>(datatempCPU));
		 storeage.add(new ArrayList<Double>(datatempMEM)); 	 
		 storeage.add(new ArrayList<Double>(datatempDISK)); 	
		 storeage.add(new ArrayList<Double>(datatempNET));
		 datatempCPU.clear(); 	 	datatempMEM.clear();     datatempDISK.clear(); 	 	datatempNET.clear();
		 	 
		 System.out.println("\n");
		 
		 
		 try {
			 CSVFile.close();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	}

	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
}
