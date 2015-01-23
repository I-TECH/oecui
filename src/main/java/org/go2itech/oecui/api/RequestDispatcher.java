/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is OpenEMRConnect.
 *
 * The Initial Developer of the Original Code is International Training &
 * Education Center for Health (I-TECH) <http://www.go2itech.org/>
 *
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK ***** */
package org.go2itech.oecui.api;

import java.util.logging.Level;
import java.util.logging.Logger;
import ke.go.moh.oec.RequestTypeId;
import ke.go.moh.oec.lib.Mediator;
import org.go2itech.oecui.data.RequestResult;
import org.go2itech.oecui.data.Server;

/**
 * The RequestDispatcher class provides a unified way of dispatching OEC client
 * requests to OEC servers. See {@link RequestTypeId} in oeclib.
 * <p>
 * Depending on the {@link RequestDispatcher.MessageType} and {@link Server}
 * specified, it creates and spawns the necessary number of
 * {@link RequestDispatchingThread}s to satisfy the requested operation.
 *
 * @author Gitahi Ng'ang'a
 */
public class RequestDispatcher {

    public class MessageType {

        /**
         * Signals the RequestDispatcher to dispatch a FIND_PERSON request to a
         * Person Index or Indices. See {@link MessageTypeRegistry} in oeclib.
         * <p>
         * This value makes no assumptions about the index or indices to
         * contact. That must be specified separately by setting the value
         * {@link Server}
         */
        public static final int FIND = 1;
        /**
         * Signals the RequestDispatcher to dispatch a CREATE_PERSON request to
         * a Person Index or Indices. See {@link MessageTypeRegistry} in oeclib.
         * <p>
         * This value makes no assumptions about the index or indices to
         * contact. That must be specified separately by setting the value
         * {@link Server}
         */
        public static final int CREATE = 2;
        /**
         * Signals the RequestDispatcher to dispatch a MODIFY_PERSON request to
         * a Person Index or Indices. See {@link MessageTypeRegistry} in oeclib.
         * <p>
         * This value makes no assumptions about the index or indices to
         * contact. That must be specified separately by setting the value
         * {@link Server}
         */
        public static final int MODIFY = 3;
    }
    /**
     * The Mediator instance used to satisfy a client request
     */
    private static final Mediator mediator = new Mediator();

    /**
     * This method behaves the same as {@link RequestDispatcher#dispatch(ke.go.moh.oec.reception.controller.Object,
     * ke.go.moh.oec.reception.data.RequestResult, ke.go.moh.oec.reception.data.RequestResult, int, int) with the exception
     * that no response(s) are expected.
     *
     * @param requestData object containing data for the request
     * @param dispatchType the {@link MessageType} of request to be dispatched
     * @param targetServer the {@link Server}s to be contacted
     */
    public static void dispatch(Object requestData,
            int dispatchType, int targetServer) {
        dispatch(requestData, null, null, dispatchType, targetServer);
    }

    /**
     * Dispatches a request of a specific type to a either the MPI, the LPI or
     * both.
     * <p>
     * If the dispatchType specified is
     * {@link RequestDispatcher.DispatchType#FIND}, any
     * {@link RequestDispatchingThread}s spawned join the current thread and
     * cause this method to wait until they return. Otherwise, if the
     * dispatchType specified is {@link RequestDispatcher.DispatchType#CREATE}
     * or {@link RequestDispatcher.DispatchType#MODIFY} any
     * {@link RequestDispatchingThread}s spawned do not join the current thread
     * and this method returns immediately. Any other dispatchType values are
     * ignored and no {@link RequestDispatchingThread}s are started.
     * <p>
     * Also, the number of {@link RequestDispatchingThread}s spawned depends on
     * the {@link Server} specified. Specifically, two
     * {@link RequestDispatchingThread}s are spawned for {@link Server#MPI_LPI}
     * and only one otherwise.
     *
     * @param requestData object containing data for the request
     * @param mpiRequestResult object to contain the MPI results of the request
     * @param lpiRequestResult object to contain the LPI results of the request
     * @param dispatchType the {@link MessageType} of request to be dispatched
     * @param targetServer the {@link Server}s to be contacted
     */
    public static void dispatch(Object requestData, RequestResult mpiRequestResult,
            RequestResult lpiRequestResult,
            int dispatchType, int targetServer) {
        boolean join = (dispatchType == MessageType.FIND);
        if (targetServer == Server.MPI_LPI) {
            if (dispatchType == MessageType.FIND) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.FIND_PERSON_MPI, requestData, mpiRequestResult), join);
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.FIND_PERSON_LPI, requestData, lpiRequestResult), join);
            } else if (dispatchType == MessageType.CREATE) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.CREATE_PERSON_MPI, requestData, mpiRequestResult), join);
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.CREATE_PERSON_LPI, requestData, lpiRequestResult), join);
            } else if (dispatchType == MessageType.MODIFY) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.MODIFY_PERSON_MPI, requestData, mpiRequestResult), join);
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.MODIFY_PERSON_LPI, requestData, lpiRequestResult), join);
            }
        } else if (targetServer == Server.MPI) {
            if (dispatchType == MessageType.FIND) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.FIND_PERSON_MPI, requestData, mpiRequestResult), join);
            } else if (dispatchType == MessageType.CREATE) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.CREATE_PERSON_MPI, requestData, mpiRequestResult), join);
            } else if (dispatchType == MessageType.MODIFY) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.MODIFY_PERSON_MPI, requestData, mpiRequestResult), join);
            }
        } else if (targetServer == Server.LPI) {
            if (dispatchType == MessageType.FIND) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.FIND_PERSON_LPI, requestData, lpiRequestResult), join);
            } else if (dispatchType == MessageType.CREATE) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.CREATE_PERSON_LPI, requestData, lpiRequestResult), join);
            } else if (dispatchType == MessageType.MODIFY) {
                spawn(new RequestDispatchingThread(
                        mediator, RequestTypeId.MODIFY_PERSON_LPI, requestData, lpiRequestResult), join);
            }
        }
    }

    /*
     * Spawn a new thread and specify whether or not it should join
     * the current one.
     */
    private static void spawn(RequestDispatchingThread rdt, boolean join) {
        rdt.start();
        if (join) {
            try {
                rdt.join();
            } catch (Exception ex) {
                Logger.getLogger(RequestDispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
