
package cf.dashika.poshtamp.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TypePoshtamps {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("services")
    @Expose
    private List<ServiceForGetTypes> services = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ServiceForGetTypes> getServices() {
        return services;
    }

    public void setServices(List<ServiceForGetTypes> services) {
        this.services = services;
    }

}
