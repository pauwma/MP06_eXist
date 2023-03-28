package net.xeill.elpuig;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;

import javax.xml.xquery.XQException;
import javax.xml.xquery.XQResultSequence;

public class Main {

    public static void main(String[] args) throws XQException {
        ExistController ec = new ExistController();
        XQResultSequence xqrs = ec.executeQuery("for $book in doc('/db/apps/foaf/foaf/books.xml')/library/book where $book/author='Stephen King' return $book/title");
        ec.printResultSequence(xqrs);
        xqrs = ec.executeQuery("for $book in doc('/db/apps/foaf/books.xml')/library/book where $book/year < 1960 return $book");
        ec.printResultSequence(xqrs);

    }
}

