import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    public static final double FEET_PER_LONGTITUDE = 288200;
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.
    static QuadTree root = new QuadTree(MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT,
            MapServer.ROOT_LRLON, MapServer.ROOT_LRLAT);
    String imgPath;

    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        imgPath = imgRoot;
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>Has dimensions of at least w by h, where w and h are the user viewport width
     *         and height.</li>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        double lrlon = params.get("lrlon");
        double ullon = params.get("ullon");
        double lrlat = params.get("lrlat");
        double ullat = params.get("ullat");
        double w = params.get("w");
        double h = params.get("h");

        double lonDPP = (lrlon - ullon) / w * FEET_PER_LONGTITUDE;

        double rootLonDPP = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON)
                / 256 * FEET_PER_LONGTITUDE;
        int level = 0;
        while (rootLonDPP > lonDPP) {
            level += 1;
            rootLonDPP = rootLonDPP / 2;
        }

        if (level > 7) {
            level = 7;
        }

        //System.out.println(level);

        PriorityQueue<QuadTree.Node> imgHeap = new PriorityQueue<>();
        HashSet<Double> lon = new HashSet<>();
        root.searchHelper(root.getRoot(), ullon, ullat, lrlon, lrlat, level, imgHeap, lon);
        //System.out.println(imgHeap.size());

        int colNum = lon.size();
        int rowNum = imgHeap.size() / colNum;
        String[][] imgArray = new String[rowNum][colNum];
        double rasterUllon = imgHeap.peek().ullon;
        double rasterUllat = imgHeap.peek().ullat;
        double rasterLrlon = 0;
        double rasterLrlat = 0;

        for (int i = 0; i < rowNum; i += 1) {
            for (int j = 0; j < colNum; j += 1) {
                if (i == rowNum - 1 && j == colNum - 1) {
                    rasterLrlon = imgHeap.peek().lrlon;
                    rasterLrlat = imgHeap.peek().lrlat;
                }
                imgArray[i][j] = imgPath + imgHeap.poll().imageName + ".png";
            }
        }

        Map<String, Object> results = new HashMap<>();


        results.put("render_grid", imgArray);
        results.put("raster_ul_lon", rasterUllon);
        results.put("raster_ul_lat", rasterUllat);
        results.put("raster_lr_lon", rasterLrlon);
        results.put("raster_lr_lat", rasterLrlat);
        results.put("depth", level);
        results.put("query_success", true);

        return results;
    }

}
