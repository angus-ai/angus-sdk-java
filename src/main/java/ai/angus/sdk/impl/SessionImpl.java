package ai.angus.sdk.impl;

import java.util.UUID;

import org.json.simple.JSONObject;

import ai.angus.sdk.Service;
import ai.angus.sdk.Session;

public class SessionImpl implements Session {

    private Service service;
    private String  id;

    public SessionImpl(Service service) {
        this.service = service;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public JSONObject getState() {
        JSONObject state=new JSONObject();
        state.put("session_id", this.id);
        return state;
    }

}
