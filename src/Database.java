import java.util.ArrayList;
import java.util.Random;


public class Database {
	static ArrayList<User> UserList;
	static ArrayList<Movie> MovieList;
	
	static {
		Random rndNumbers = new Random();
		UserList = new ArrayList<User>();
		MovieList = new ArrayList<Movie>();
        for (int i = 0; i < 30; i++) {   
            UserList.add(new User((10 + rndNumbers.nextInt(Constants.Age - 10)),rndNumbers.nextInt(Constants.gender.size()),rndNumbers.nextInt(Constants.occupation.size())));
        }
        
        for (int i = 0; i < 50; i++) {   
            MovieList.add(new Movie(calRating(i),i));
        }
    }
	
	public static double calRating(int index){
		double sum=0,count=0;
		double rating = 0;
		for (int i = 0; i < 30; i++) {   
            if(UserList.get(i).ratings[index]!=0){
            	sum += UserList.get(i).ratings[index];
            	count++;
            }
        }
		
		if(count!=0)
			rating = sum/count;
		return rating;
	}
	
	public static ArrayList<User> getUserList(){
		return UserList;
	}
	
	public static ArrayList<Movie> getMovieList(){
		return MovieList;
	}
	
}
