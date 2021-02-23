package proyecto.transporte.udb;

public class itemModel {
    int image;
    String status;
    String routeN;
    String type;

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public int getImage(){
        return image;
    }
    public void setImage(int image){
        this.image = image;
    }
    public String getStatus(){ return status; }
    public void setStatus(String status){
        this.status = status;
    }
    public String getRouteN(){
        return routeN;
    }
    public void setRouteN(String routeN){
        this.routeN = routeN;
    }
}
