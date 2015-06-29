package ai.angus.sdk.impl;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.simple.JSONObject;

import ai.angus.sdk.Configuration;
import ai.angus.sdk.Job;
import ai.angus.sdk.ProcessException;
import ai.angus.sdk.Resource;
import ai.angus.sdk.ResultCallBack;
import ai.angus.sdk.Service;
import ai.angus.sdk.Session;

public class CompositeImpl extends ServiceImpl implements Service {

    private Collection<Service> services;
    private RootImpl            root;

    public CompositeImpl(URL parent, String name, Collection<Service> services,
            Configuration conf) {
        this(parent, name, null, services, conf);

    }

    public CompositeImpl(URL parent, String name, JSONObject content,
            Collection<Service> services,
            Configuration conf) {
        super(parent, name, content, conf);
        this.services = services;
        this.root = new RootImpl(conf);
    }

    /*
     * (non-Javadoc)
     *
     * @see ai.angus.sdk.impl.ServiceImpl#process(org.json.simple.JSONObject,
     * boolean, ai.angus.sdk.Session, ai.angus.sdk.ResultCallBack)
     */
    @Override
    public Job process(JSONObject params, boolean async, Session session,
            ResultCallBack callback) throws ProcessException {

        params = new JSONObject(params);
        params.put("async", async);

        if (session == null) {
            session = this.defaultSession;
        }

        if (session != null) {
            params.put("state", session.getState());
        }

        // TODO: only root is converted
        for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
            Map.Entry o = (Entry) i.next();
            if (o.getValue() instanceof File) {
                Resource res = this.root.getBlobs().create((File) o.getValue());
                URL resUrl = res.getURL();
                o.setValue(resUrl.toString());
            }
        }

        //ArrayList<Pair<String, Future<JSONObject>>> futures = new ArrayList<Future<JSONObject>>();

        Map<String, Future<JSONObject>> futures = new HashMap<String, Future<JSONObject>>();

        /* Send in parallel */
        for (Iterator<Service> i = services.iterator(); i.hasNext();) {
            Service service = i.next();
            Future<JSONObject> future = this.conf.getHTTPClient().nbPost(
                    service.getJobs().getURL(),
                    params.toJSONString().getBytes());
            futures.put(service.getName(), future);
        }

        JSONObject composite = new JSONObject();

        for (Map.Entry<String, Future<JSONObject>> future : futures.entrySet()) {
            JSONObject o;
            try {
                o = future.getValue().get();
                if ((Long) o.get("status") < 400) {
                    composite.put(future.getKey(), o);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        JSONObject result = new JSONObject();

        result.put("status", 200);
        result.put("composite", composite);

        JobImpl job = new JobImpl(this.endpoint, "", result, this.conf);

        return job;
    }

}
