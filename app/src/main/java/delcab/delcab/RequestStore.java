package delcab.delcab;

import java.util.ArrayList;
import java.util.List;

public class RequestStore {
    private static RequestStore store;

    //make this static ?? and the methods
    public List<String> responseList = new ArrayList<>();



    /**
     * The private constructor for the Customer List Singleton class
     */
    private RequestStore() {}

    public static RequestStore getRequest() {
        //instantiate a new CustomerLab if we didn't instantiate one yet
        if (store == null) {
            store = new RequestStore();
        }
        return store;
    }
    //add methods here for insert, delete, search etc......

    public void addResponse(String response){
        responseList.add(response);
    }





}