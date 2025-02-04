package metier;

record Point(int num, int demande, float x, float y) {

    public float getDistance(Point c) {
        return (float) Math.sqrt(Math.pow(this.x - c.x, 2) + Math.pow(this.y - c.y, 2));
    }
    

    public String toString() {
        return "Point [num=" + num + ", demande=" + demande + ", x=" + x + ", y=" + y + "]";
    }
}