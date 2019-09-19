import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */

public class Rasterer {
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    private double depthArr_y[] = new double[8];
    public Rasterer() {
        double height = ROOT_LRLON - ROOT_ULLON;
        depthArr_y[0] = height / 256;


        for(int i = 1; i < depthArr_y.length; ++i) {
            height /= 2;
            depthArr_y[i] =  height / 256;
        }
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();
        results.put("query_success", true);
        double lrlon = params.get("lrlon"),
                ullon = params.get("ullon"),
                w = params.get("w"),
                h = params.get("h"),
                ullat = params.get("ullat"),
                lrlat = params.get("lrlat");
        if (lrlon < MapServer.ROOT_ULLON || lrlat > MapServer.ROOT_ULLAT
                || ullon > MapServer.ROOT_LRLON || ullat < MapServer.ROOT_LRLAT
                || ullon > lrlon || ullat < lrlat) {
            results.put("query_success", false);
        }

        if(!(boolean) (results.get("query_success"))) return results;
        ullon = (ullon < MapServer.ROOT_ULLON) ? MapServer.ROOT_ULLON : ullon;
        ullat = (ullat > MapServer.ROOT_ULLAT) ? MapServer.ROOT_ULLAT : ullat;
        lrlon = (lrlon > MapServer.ROOT_LRLON) ? MapServer.ROOT_LRLON : lrlon;
        lrlat = (lrlat < MapServer.ROOT_LRLAT) ? MapServer.ROOT_LRLAT : lrlat;

        double desire_LonDPP = (lrlon - ullon) / w;
        int depth = 0;
        if(desire_LonDPP < depthArr_y[7]) {
            depth = 7;
        } else {
            for(int i = 0; i < depthArr_y.length; ++i) {
                if (desire_LonDPP > depthArr_y[i]) {
                    depth = i;
                    break;
                }
            }
        }

        results.put("depth", depth);

        double ul_x = (ROOT_LRLON - ROOT_ULLON) / Math.pow(2.0, depth);
        double ul_y = (ROOT_LRLAT - ROOT_ULLAT) / Math.pow(2.0, depth);
        int UL_img_x = (int) ((ullon - ROOT_ULLON) / ul_x);
        int UL_img_y = (int) ((ullat - ROOT_ULLAT) / ul_y);
        int LR_img_x = (int) ((lrlon - ROOT_ULLON) / ul_x) + 1;
        int LR_img_y = (int) ((lrlat - ROOT_ULLAT) / ul_y) + 1;
        String s [][] = new String[LR_img_y - UL_img_y][LR_img_x - UL_img_x];
        for(int i = 0; i < LR_img_y - UL_img_y; ++i) {
            for(int j = 0; j < LR_img_x - UL_img_x; ++j) {
                s[i][j] = "d" + depth + "_x" + (UL_img_x + j) + "_y" + (UL_img_y + i) +".png";
            }
        }
        results.put("render_grid", s);
        results.put("raster_ul_lon", UL_img_x*ul_x + ROOT_ULLON);
        results.put("raster_ul_lat", UL_img_y*ul_y + ROOT_ULLAT);
        results.put("raster_lr_lon", LR_img_x*ul_x + ROOT_ULLON);
        results.put("raster_lr_lat", LR_img_y*ul_y + ROOT_ULLAT);

        return results;
    }

}
