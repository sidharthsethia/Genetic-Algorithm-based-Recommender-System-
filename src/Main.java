import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class Main {
	public ArrayList<User> UserList;
	public UserFold folds[] = new UserFold[3];
	public ArrayList<Movie> MovieList;
	public double systemMAE = 0;
	public static void main(String args[]){
		Main obj = new Main();
		obj.initializeLists();
		obj.writeToFile();
		obj.createFolds();
		obj.executeFolds();
	}
	
	public void initializeLists(){
		UserList = Database.getUserList();
		MovieList = Database.getMovieList();
	}
	
	public void writeToFile(){
		try {
			File file = new File("GAA_Database.txt");

			
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append("USERS" + "\n\n");
			for(int i=0;i<UserList.size();i++){
				String content = "User " + (i+1) + ": " + UserList.get(i).Age + " " + 
						UserList.get(i).gender + " " + UserList.get(i).Occupation + " ";
				for(int j=0;j<50;j++){
					content += UserList.get(i).ratings[j] + " "; 
				}
				bw.append(content + "\n");
			}
			bw.append("\n\nMOVIES\n\n");
			for(int i=0;i<MovieList.size();i++){
				String content = "Movie " + (i+1) + ": " + MovieList.get(i).genreList +" \n";;
				bw.append(content);
			}
			
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void writeToFile(int fold){
		try {
			File file = new File("GAA_FOLD" + (fold+1) +".txt");

			
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.append("FOlD " + (fold+1) +": \n");
			
			for(int i=0;i<10;i++){
				String content = "User " + (i+1) + ": \n" + "MAE - " + folds[fold].UserList.get(i).MAE + " ";
				/*for(int k=0;k<18;k++){
					content += folds[fold].UserList.get(i).GIM[k] + " ";
				}*/
				content +="\nNeigbour 1: "+ folds[fold].UserList.get(i).neighbourList.get(0).index + "_" +folds[fold].UserList.get(i).neighbourList.get(0).distance + " "
			+"Neigbour 2: "+ folds[fold].UserList.get(i).neighbourList.get(1).index + "_" +folds[fold].UserList.get(i).neighbourList.get(1).distance + " "
			+"Neigbour 3: "+ folds[fold].UserList.get(i).neighbourList.get(2).index + "_" +folds[fold].UserList.get(i).neighbourList.get(2).distance + " "
			+"Neigbour 4: "+ folds[fold].UserList.get(i).neighbourList.get(3).index + "_" +folds[fold].UserList.get(i).neighbourList.get(3).distance + " "
			+"Neigbour 5: "+ folds[fold].UserList.get(i).neighbourList.get(4).index + "_" +folds[fold].UserList.get(i).neighbourList.get(4).distance + "\n\n";
				
				bw.append(content);
			}
			bw.append("Fold MAE: " + folds[fold].MAE + "\n");
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createFolds(){
		for(int i=0;i<3;i++){
			folds[i] = new UserFold(); 
			folds[i].UserList = new ArrayList<User>(UserList);
	        Collections.shuffle(folds[i].UserList);
		}
	}
	
	public void executeFolds(){
		double foldMAEsum = 0;
		for(int i=0;i<3;i++){
			calGIM(i);
			calFuzzyDistance(i);
			calMAE(i);
			foldMAEsum += folds[i].MAE;
			System.out.println("Mean Absolute Error for fold " + (i+1) + ": " + folds[i].MAE);
			writeToFile(i);
		}
		systemMAE = foldMAEsum/3;
		System.out.println("Mean Absolute Error for the System: " + systemMAE);
	}
	
	private void calMAE(int fold) {
		double MAE_sum = 0;
		for(int p=0;p<10;p++){
			User user = folds[fold].UserList.get(p);
			double diff = 0;
			for(int q=0;q<user.testingSet.size();q++){
				double pr_rating = 0;
				double distanceSum = 0;
				for(int r=0;r<5;r++){
					distanceSum += user.neighbourList.get(r).distance;
					//System.out.print(user.neighbourList.get(r).distance + " ");
					pr_rating += user.neighbourList.get(r).distance*
							(folds[fold].UserList.get(user.neighbourList.get(r).index).ratings[user.testingSet.get(q).rating]
									-folds[fold].UserList.get(user.neighbourList.get(r).index).mean_rating);
				}
				
				pr_rating = pr_rating/distanceSum;
				pr_rating += user.mean_rating;
				//System.out.println("Distance Sum: " + distanceSum + " User Weights " + user.Weights);
				//System.out.println("Predicted Rating " + pr_rating + " Actual rating: " + user.trainingSet.get(q).rating);
				diff += Math.abs(user.trainingSet.get(q).rating-pr_rating);
				
				//for(int k=0;k<21;k++){
					//System.out.print(user.Weights[k]);
				//}
			}
			user.MAE = diff/user.trainingSet.size();
			MAE_sum += user.MAE; 
		}
		folds[fold].MAE = MAE_sum/10;
	}

	public void calGIM(int fold){
		for(int i=0;i<30;i++){
			GenreRating genreRatings[] = new GenreRating[18];
			for(int p=0;p<genreRatings.length;p++){
				genreRatings[p] = new GenreRating();
			}
			User user = folds[fold].UserList.get(i);
			user.clearLists();
			user.generateSets();
			double total_ratings = 0;
			int total_count = 0;
			ArrayList<MovieSeen> set;
			if(i<10)
				set = user.trainingSet;
			else
				set = user.seenMovies;
			for(int j=0;j<set.size();j++){
				MovieSeen movie = set.get(j);
				total_ratings += movie.rating;
				total_count++;
				//System.out.println(i + " Movie Index "+ movie.rating + " " + total_ratings + " " + total_count);
				if(movie.rating>2){
					//System.out.println(MovieList.get(movie.index).genreList);
					for(int k=0;k<MovieList.get(movie.index).genreList.size();k++){
						genreRatings[MovieList.get(movie.index).genreList.get(k)].total_ratings += movie.rating;
						genreRatings[MovieList.get(movie.index).genreList.get(k)].total_counts++;
						
						if(movie.rating==3)
							genreRatings[MovieList.get(movie.index).genreList.get(k)].rating3++;
						else if(movie.rating==4)
							genreRatings[MovieList.get(movie.index).genreList.get(k)].rating4++;
						else
							genreRatings[MovieList.get(movie.index).genreList.get(k)].rating5++;
					}
				}
			}
			
			for(int p=0;p<genreRatings.length;p++){	
				double RGR = genreRatings[p].total_ratings/total_ratings;
				double MGRF1 = (genreRatings[p].rating3 + (2*genreRatings[p].rating4)+ (3*genreRatings[p].rating5));
				double MGRF = MGRF1/set.size();
				if(RGR==0 && MGRF==0)
					user.GIM[genreRatings[p].index] = 0;
				else
					user.GIM[genreRatings[p].index] = (2*Constants.nf*RGR*MGRF)/(RGR + MGRF);
				//System.out.println("User " + i + ": GIM for " + p + ": " + user.GIM[genreRatings[p].index]);
				user.mean_rating = total_ratings/total_count;
			}
			user.generateFuzzyGIMSets();
		}
	}
	
	public void calFuzzyDistance(int fold){
		for(int i=0;i<10;i++){
			for(int j=10;j<30;j++){
				for(int k=0;k<18;k++){
					double d1 = folds[fold].UserList.get(i).GIM[k] - folds[fold].UserList.get(j).GIM[k];
					double d2=0;
					for(int l=0;l<6;l++){
						d2 += Math.pow(folds[fold].UserList.get(i).fuzzyGIM[k][l] - folds[fold].UserList.get(j).fuzzyGIM[k][l], 2);
					}
					
					d2 = Math.sqrt(d2);
					folds[fold].UserList.get(i).fuzzyDistance[j-10][k]=d2*d1;
					
				}
				double d1 = folds[fold].UserList.get(i).Age - folds[fold].UserList.get(j).Age; 
				double d2 = 0;
				for(int l=0;l<3;l++){
					d2 += Math.pow(folds[fold].UserList.get(i).fuzzyAge[l] - folds[fold].UserList.get(j).fuzzyAge[l], 2);
				}
				d2 = Math.sqrt(d2);
				folds[fold].UserList.get(i).fuzzyDistance[j-10][18]=d2*d1;
				if(folds[fold].UserList.get(i).gender.equals(folds[fold].UserList.get(j).gender))
					folds[fold].UserList.get(i).fuzzyDistance[j-10][19]=1;
				else
					folds[fold].UserList.get(i).fuzzyDistance[j-10][19]=0;
				
				if(folds[fold].UserList.get(i).Occupation.equals(folds[fold].UserList.get(j).Occupation))
					folds[fold].UserList.get(i).fuzzyDistance[j-10][20]=1;
				else
					folds[fold].UserList.get(i).fuzzyDistance[j-10][20]=0;
				
				//for(int k=0;k<21;k++){
					//System.out.print(folds[fold].UserList.get(i).fuzzyDistance[j-10][k] + " ");
					
				//}
				/*for(int k=0;k<18;k++){
					System.out.print(folds[fold].UserList.get(i).GIM[k] + " ");
					
				}*/
				
				
				//System.out.println("\n///////////////////////////////////////////");
				folds[fold].UserList.get(i).Weights = new GA(folds[fold].UserList.get(i),folds[fold].UserList).calWeights();	
			}
			
		}
	}

}
