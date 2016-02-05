import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class User {
	int Age;
	String gender;
	double GIM[]=new double[18];
	String Occupation;
	double mean_rating;
	int ratings[]=new int[50];
	double fuzzyAge[]=new double[3];;
	double fuzzyDistance[][]=new double[20][21];
	ArrayList<Neighbour> neighbourList = new ArrayList<Neighbour>();
	double Weights[]=new double[21];
	double fuzzyGIM[][] = new double[18][6];
	double MAE;
	ArrayList<MovieSeen> seenMovies = new ArrayList<MovieSeen>();
	ArrayList<MovieSeen> trainingSet = new ArrayList<MovieSeen>();
	ArrayList<MovieSeen> testingSet = new ArrayList<MovieSeen>();
	
	public User(int age, int gender, int occupation){
		this.Age = age;
		this.gender = Constants.gender.get(gender);
		this.Occupation = Constants.occupation.get(occupation);
		if(age<20){
			fuzzyAge[0] = 1;
			fuzzyAge[2] = 0;
			fuzzyAge[1] = 0;
		}
		else if(age>=20 && age<35){
			fuzzyAge[0] = (35-age)/15;
			fuzzyAge[2] = 0;
			fuzzyAge[1] = (age-20)/15;
		}
		else if(age>=35 && age<45){
			fuzzyAge[0] = 0;
			fuzzyAge[2] = 0;
			fuzzyAge[1] = 1;
		}
		else if(age>=45 && age<60){
			fuzzyAge[0] = 0;
			fuzzyAge[2] = (age-45)/15;
			fuzzyAge[1] = (60-age)/15;
		}
		else{
			fuzzyAge[0] = 0;
			fuzzyAge[2] = 1;
			fuzzyAge[1] = 0;
		}
		generateRatings();
	}
	
	public void clearLists(){
		trainingSet.clear();
		testingSet.clear();
		for (int i=0; i<18; i++) {
            this.GIM[i] = 0;
            for (int j=0; j<6; j++) {
                this.fuzzyGIM[i][j] = 0; 
            }
        }
		
	}
	
	public void generateRatings(){
		ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=0; i<50; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        Random rndNumbers = new Random();
        for (int i=0; i<(11 + rndNumbers.nextInt(40)); i++) {
            this.ratings[list.get(i)] = 1 + rndNumbers.nextInt(5); 
            seenMovies.add(new MovieSeen(list.get(i), 1 + rndNumbers.nextInt(5)));
        }
	}
	
	public void generateSets(){
		ArrayList<MovieSeen> list = new ArrayList<MovieSeen>(seenMovies);
        Collections.shuffle(list);
        int size = (int) Math.round(list.size()*0.67);
        for (int i=0; i<list.size(); i++) {
            if(i<size){
            	trainingSet.add(seenMovies.get(i));
            }
            else{
            	testingSet.add(seenMovies.get(i));
            }
        }
	}
	
	public void generateFuzzyGIMSets(){
		for (int i=0; i<GIM.length; i++) {
			double x = GIM[i];
			fuzzyGIM[i][5] = 0;
			if(x<=1){
				fuzzyGIM[i][0] = 1-x;  
			}
			else{
				fuzzyGIM[i][0] = 0;
				if(x>4)
					fuzzyGIM[i][5] = x-4;
			}
			
			for(int k=1;k<5;k++){
				fuzzyGIM[i][k]  = 0;
				if(x>k-2 && x<=k-1)
					fuzzyGIM[i][k]  = x-k+2;
				if(x>k-1 && x<=k)
					fuzzyGIM[i][k]  = k-x;
			}
        }
	}
}
