package delcab.delcab;

import java.util.ArrayList;
import java.util.List;

public class RequestStore {
    private static RequestStore store;

    public List<String> responseList = new ArrayList<>();


    private RequestStore() {}

    public static RequestStore getRequest() {

        if (store == null) {
            store = new RequestStore();
        }
        return store;
    }


    public void addResponse(String response){
        responseList.add(response);
    }





}