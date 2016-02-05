
public class Neighbour implements Comparable<Neighbour> {
	double distance;
	int index;
	@Override
	public int compareTo(Neighbour o) {
		return (int) ((this.distance - o.distance)*100000);
	}
}
