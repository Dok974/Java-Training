/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.doranco.controllers;

import com.doranco.dao.DaoFactory;
import com.doranco.dao.iinterface.UtilisateurDaoInterface;
import com.doranco.entities.Utilisateur;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Base64;

/**
 *
 * @author Admin
 */
@Provider
public class RequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
         System.out.println("Execution de ContainerRequestFilter premier");
         //recup value autho
         String basicAuth = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
         basicAuth = basicAuth.replace("Basic ", "");
         //Decodage et recup 
         String authDecode = new String(Base64.getDecoder().decode(basicAuth));
         System.out.println(authDecode);
         
         String[] credentials = authDecode.split(":");
         String username = credentials[0];
         String password = credentials[1];
         
         Utilisateur utilisateur = new Utilisateur(username,password);
         DaoFactory daoFactory = new DaoFactory();
         UtilisateurDaoInterface utilisateurDaoInterface = daoFactory.getUtilisateurDaoInterface();
         
         utilisateur = utilisateurDaoInterface.loginUtilisateur(utilisateur);
         
                 
         System.out.println(username);
         if((utilisateur != null)){
             String urlPath = requestContext.getUriInfo().getPath();
             if(urlPath.contains("admin")){
                 if (utilisateur.isAdmin()){
                     return;
                 }else{
                     Response response = Response.status(Response.Status.FORBIDDEN).entity("T'es pas Admin COCO").build(); 
                     requestContext.abortWith(response);
                 }
             }
             return;
         }
         Response response = Response.status(Response.Status.FORBIDDEN).entity("Access refuse").build(); 
         requestContext.abortWith(response);
         
    }
}
