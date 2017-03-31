
package cf.dashika.poshtamp.Model;

import android.os.ParcelFormatException;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

public class Postamat implements ClusterItem {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("branch")
    @Expose
    private String branch;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("address_ua")
    @Expose
    private String addressUa;
    @SerializedName("addrid")
    @Expose
    private String addrid;
    @SerializedName("point")
    @Expose
    private Point point;
    @SerializedName("schedule")
    @Expose
    private Schedule schedule;
    @SerializedName("placed_in")
    @Expose
    private String placedIn;
    @SerializedName("services")
    @Expose
    private List<Service> services = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressUa() {
        return addressUa;
    }

    public void setAddressUa(String addressUa) {
        this.addressUa = addressUa;
    }

    public String getAddrid() {
        return addrid;
    }

    public void setAddrid(String addrid) {
        this.addrid = addrid;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getPlacedIn() {
        return placedIn;
    }

    public void setPlacedIn(String placedIn) {
        this.placedIn = placedIn;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @Override
    public LatLng getPosition() {

            double lat = Double.parseDouble(point.getLatitude());
            double lng = Double.parseDouble(point.getLongitude());
            return new LatLng(lat,lng);
    }
}
