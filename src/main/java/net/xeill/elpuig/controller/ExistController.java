package net.xeill.elpuig.controller;

import net.xqj.exist.ExistXQDataSource;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;


public class ExistController {
    private XQConnection connection;
    public ExistController() {
        try {
            XQDataSource xqs = new ExistXQDataSource();
            xqs.setProperty("serverName", "localhost");
            xqs.setProperty("port", "8080");
            xqs.setProperty("user", "admin");
            xqs.setProperty("password", "2506");
            connection = xqs.getConnection();

        } catch (XQException e) {
            throw new RuntimeException(e);
        }
    }

    public XQResultSequence executeQuery(String query) {
        try {
            XQExpression xqe = connection.createExpression();
            XQResultSequence xqrs = xqe.executeQuery(query);
            return xqrs;
        } catch (XQException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeNonReturningQuery(String query) {
        try {
            XQExpression xqe = connection.createExpression();
            xqe.executeCommand(query);
        } catch (XQException e) {
            throw new RuntimeException(e);
        }
    }

    public XQConnection getConnection() {
        return connection;
    }

    public void printResultSequence(XQResultSequence xqrs) {
        try {
            while (xqrs.next()) {
                System.out.println(xqrs.getItemAsString(null));
            }
        } catch (XQException e) {
            throw new RuntimeException(e);
        }
    }

}