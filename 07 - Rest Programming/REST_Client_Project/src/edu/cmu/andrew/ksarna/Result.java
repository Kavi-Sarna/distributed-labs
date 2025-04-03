package edu.cmu.andrew.ksarna;

// A simple class to wrap an RPC result.
class Result {

    private int responseCode;
    private String responseText;

    public int getResponseCode() {
        return responseCode;
    }
    public void setResponseCode(int code) {
        responseCode = code;
    }

    public String getResponseText() {
        return responseText;
    }
    public void setResponseText(String msg) {
        responseText = msg;
    }

    @Override
    public String toString() {
        return responseCode + ": " + responseText;
    }
}
