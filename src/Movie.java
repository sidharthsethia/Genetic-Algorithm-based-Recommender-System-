import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Movie {
	ArrayList<Integer> genreList = new ArrayList<Integer>(); 
	int genre[]=new int[18];
	int index;
	double rating;
	
	public Movie(double rating, int index){
		this.rating = rating;
		this.index = index;
		for (int i=0; i<18; i++) {
            this.genre[i] = 0; 
        }
		generateGenres();

	}
	
	public void generateGenres(){
		ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=0; i<18; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        Random rndNumbers = new Random();
        for (int i=0; i<(2 + rndNumbers.nextInt(6)); i++) {
            this.genre[list.get(i)] = 1;
            genreList.add(list.get(i));
        }
	}
	
}
