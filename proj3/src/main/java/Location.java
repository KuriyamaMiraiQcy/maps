/**
 * Created by 62339 on 2017/4/14.
 */
class Location {
    String name;
    double lat;
    double lon;
    long id;

    Location(GraphNode a, String name) {
        this.id = a.id;
        this.lat = a.lat;
        this.lon = a.lon;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        Location temp = (Location) obj;
        return this.id == temp.id && this.lat == temp.lat && this.lon == temp.lon && this.name == temp.name;
    }
}
