import java.util.Random;

/**
 * The world for this project. We have a Skip List and a Bintree
 *
 * @author adsleptsov
 * @version Fall 2025
 */
public class WorldDB implements ATC {
    private final int worldSize = 1024;
    private Random rnd;

    private SkipList<String, AirObject> skipList;
    private Bintree bintree;

    public WorldDB(Random r) {
        rnd = r;
        if (rnd == null) {
            rnd = new Random();
        }
        clear();
    }

    public void clear() {
        skipList = new SkipList<>(rnd);
        bintree = new Bintree(worldSize);
    }

    public boolean add(AirObject a) {
        if (!isValid(a)) {
            return false;
        }
        
        if (skipList.find(a.getName()) != null) {
            return false;
        }

        skipList.insert(a.getName(), a);
        bintree.insert(a);
        return true;
    }

    public String delete(String name) {
        if (name == null) {
            return null;
        }
        
        AirObject obj = skipList.remove(name);
        
        if (obj == null) {
            return null; 
        }
        
        // Fixed: Uncommented this line
        bintree.remove(obj);
        
        return obj.toString();
    }

    public String printskiplist() {
        return skipList.print();
    }

    public String printbintree() {
        return bintree.print();
    }

    public String print(String name) {
        if (name == null) {
            return null;
        }
        
        AirObject obj = skipList.find(name);
        if (obj == null) {
            return null;
        }
        
        return obj.toString();
    }

    public String rangeprint(String start, String end) {
        if (start == null || end == null) {
            return null;
        }
        if (start.compareTo(end) > 0) {
            return null;
        }
        return skipList.range(start, end);
    }

    public String collisions() {
        return bintree.collisions();
    }

    public String intersect(int x, int y, int z, int xwid, int ywid, int zwid) {
        if (x < 0 || x >= worldSize || y < 0 || y >= worldSize || z < 0 || z >= worldSize) {
            return null;
        }
        if (xwid <= 0 || xwid > worldSize || ywid <= 0 || ywid > worldSize || zwid <= 0 || zwid > worldSize) {
            return null;
        }
        if (x + xwid > worldSize || y + ywid > worldSize || z + zwid > worldSize) {
            return null;
        }

        return bintree.intersect(x, y, z, xwid, ywid, zwid);
    }

    private boolean isValid(AirObject a) {
        if (a == null || a.getName() == null || a.getName().isEmpty()) {
            return false;
        }

        int x = a.getXorig();
        int y = a.getYorig();
        int z = a.getZorig();
        int xw = a.getXwidth();
        int yw = a.getYwidth();
        int zw = a.getZwidth();

        if (x < 0 || x >= worldSize || y < 0 || y >= worldSize || z < 0 || z >= worldSize) {
            return false;
        }
        if (xw <= 0 || xw > worldSize || yw <= 0 || yw > worldSize || zw <= 0 || zw > worldSize) {
            return false;
        }

        if (x + xw > worldSize || y + yw > worldSize || z + zw > worldSize) {
            return false;
        }

        try {
            if (a instanceof AirPlane) {
                AirPlane p = (AirPlane) a;
                if (p.getCarrier() == null || p.getFlightNum() <= 0 || p.getNumEngines() <= 0) {
                    return false;
                }
            } 
            else if (a instanceof Balloon) {
                Balloon b = (Balloon) a;
                if (b.getType() == null || b.getAscentRate() < 0) {
                    return false;
                }
            }
            else if (a instanceof Bird) {
                Bird b = (Bird) a;
                if (b.getType() == null || b.getNumber() <= 0) {
                    return false;
                }
            }
            else if (a instanceof Drone) {
                Drone d = (Drone) a;
                if (d.getBrand() == null || d.getNumEngines() <= 0) {
                    return false;
                }
            }
            else if (a instanceof Rocket) {
                Rocket r = (Rocket) a;
                if (r.getAscentRate() < 0 || r.getTrajectory() < 0) {
                    return false;
                }
            }
        } 
        catch (Exception e) {
            return false;
        }
        return true;
    }
}