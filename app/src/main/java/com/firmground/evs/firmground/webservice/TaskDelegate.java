package com.firmground.evs.firmground.webservice;

import org.json.JSONException;

/**
 * Created by evs on 12/20/2016.
 */

public interface TaskDelegate {
    String TaskCompletionResult(String result) throws JSONException;
}
