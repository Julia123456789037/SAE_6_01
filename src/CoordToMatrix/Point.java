package CoordToMatrix;

record Point(int num, int demande, float x, float y) {

    public float getDistance(Point c) {
        return Math.abs(this.x - c.x) + Math.abs(this.y - c.y);
    }

    public String toString() {
        return "Point [num=" + num + ", demande=" + demande + ", x=" + x + ", y=" + y + "]";
    }
}