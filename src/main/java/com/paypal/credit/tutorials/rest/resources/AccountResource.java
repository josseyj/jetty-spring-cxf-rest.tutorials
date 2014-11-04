package com.paypal.credit.tutorials.rest.resources;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * This class defines a AccountResource.
 */
@Named
@Path("accounts/{account_id}")
public class AccountResource {

    @GET
    @Produces("application/json")
    public String version() {
        return "1.0.0";
    }
    
}
