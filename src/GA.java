import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class GA {
	ArrayList<Chromosome> chromosomes = new ArrayList<Chromosome>();
	User user;
	public ArrayList<User> NeighbourList;
	double mutation_probability = Constants.mutation_probability;
	double crossover_probability = Constants.crossover_probability;
	
	public GA(User user, ArrayList<User> list){
		this.user = user;
		this.NeighbourList = list;
	}
	
	public void initialPopulation(){
		for(int i=0;i<10;i++){
			Chromosome c = new Chromosome();
			for(int j=0;j<63;j++){
				Random rndNumbers = new Random();
				c.chromosomes[j] = Constants.alleleValues[rndNumbers.nextInt(2)];
			}
			chromosomes.add(c);
		}
	}
	
	public double[] calWeights(){
		initialPopulation();
		calculateFitness(chromosomes);
		for(int i=0;i<49;i++){
			newPopulation();
		}
		Collections.sort(chromosomes);
		this.user.neighbourList = chromosomes.get(0).neighbourList;
		return chromosomes.get(0).phenotype;
	}
	
	public void calculateFitness(ArrayList<Chromosome> chromosomes){
		for(int i=0;i<10;i++){
			int sum = 0, totalSum=0;
			for(int j=0;j<63;j++){
				if(j%3==0){
					sum += Math.pow(2, 2);
				}if(j%3==1){
					sum += Math.pow(2, 1);
				}
				else{
					sum += Math.pow(2, 0);
					chromosomes.get(i).phenotype[j/3] = sum;
					totalSum +=sum;
					sum = 0;
				}
			}
			
			for(int q=0;q<21;q++){
				chromosomes.get(i).phenotype[q] = chromosomes.get(i).phenotype[q]/totalSum;
			}

			
			for(int p=10;p<30;p++){
				Neighbour n = new Neighbour();
				n.distance = 0;
				n.index = p;
				for(int q=0;q<21;q++){
					n.distance += chromosomes.get(i).phenotype[q]*Math.pow(user.fuzzyDistance[p-10][q], 2);
				}
				n.distance = Math.sqrt(n.distance);
				chromosomes.get(i).neighbourList.add(n);
			}
			
			Collections.sort(chromosomes.get(i).neighbourList);
			double diff = 0;
			for(int p=0;p<user.trainingSet.size();p++){
				double pr_rating = 0;
				double distanceSum = 0;
				for(int q=0;q<5;q++){
					distanceSum += chromosomes.get(i).neighbourList.get(q).distance; 
					pr_rating += chromosomes.get(i).neighbourList.get(q).distance*
							(NeighbourList.get(chromosomes.get(i).neighbourList.get(q).index).ratings[user.trainingSet.get(p).index]
									-NeighbourList.get(chromosomes.get(i).neighbourList.get(q).index).mean_rating);
				}
				
				pr_rating = pr_rating/distanceSum;
				pr_rating += user.mean_rating;
				
				diff += Math.abs(user.trainingSet.get(p).rating-pr_rating);
			}
			chromosomes.get(i).fitness = diff/user.trainingSet.size(); 	
		}
	}
	
	public void newPopulation(){
		Collections.sort(chromosomes);
		ArrayList<Chromosome> children = new ArrayList<Chromosome>();
		Random rndNumbers = new Random();
		
		//Crossover
		for(int i=0;i<9;i=i+2){
			Chromosome c1 = new Chromosome();
			Chromosome c2 = new Chromosome();
			if(Math.random()<crossover_probability){
				int swap_position = rndNumbers.nextInt(63);
				for(int k=0;k<swap_position;k++){
					c1.chromosomes[k] = chromosomes.get(i).chromosomes[k];
					c2.chromosomes[k] = chromosomes.get(i+1).chromosomes[k];
				}
				for(int k=swap_position;k<c1.chromosomes.length;k++){
					c1.chromosomes[k] = chromosomes.get(i+1).chromosomes[k];
					c2.chromosomes[k] = chromosomes.get(i).chromosomes[k];
				}
			}
			children.add(c1);
			children.add(c2);
		}
		
		//Mutation
		for(int i=0;i<10;i=i+1){
			for(int j=0;j<63;j++){
				if(Math.random()<mutation_probability){
					if(children.get(i).chromosomes[j]==0)
						children.get(i).chromosomes[j]=1;
					else
						children.get(i).chromosomes[j]=0;
				}
			}
		}
		
		calculateFitness(children);
		//eliticism
		children.addAll(chromosomes);
		Collections.sort(children);
		chromosomes.clear();
		for(int i=0;i<10;i++){
			chromosomes.add(children.get(i));
		}
	}
}
