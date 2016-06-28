package org.go2itech.oecui.data;

/**
 *
 * @author gitahi
 */
public class RequestResultPair {
    
    private final RequestResult lpiResult;
    private final RequestResult mpiResult;

    public RequestResultPair(RequestResult lpiResult, RequestResult mpiResult) {
        this.lpiResult = lpiResult;
        this.mpiResult = mpiResult;
    }

    public RequestResult getLpiResult() {
        return lpiResult;
    }

    public RequestResult getMpiResult() {
        return mpiResult;
    }
}
