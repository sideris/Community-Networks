package ve;

import java.awt.geom.Point2D;

public class Vertex {

    private String id;
    public int x,y;
    //More info in the future

    public Vertex(String id,int xx, int yy){
        this.id = id;
        this.x = xx;
        this.y = yy;
    }

    public String getId() {
        return id;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString(){
        return this.id;
    }

}