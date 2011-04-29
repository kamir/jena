/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sdb.layout2.index;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hp.hpl.jena.graph.Node;

import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.compiler.QueryCompilerFactory;
import com.hp.hpl.jena.sdb.layout2.NodeLayout2;
import com.hp.hpl.jena.sdb.layout2.StoreBase;
import com.hp.hpl.jena.sdb.layout2.TableDescQuads;
import com.hp.hpl.jena.sdb.layout2.TableDescTriples;
import com.hp.hpl.jena.sdb.sql.RS;
import com.hp.hpl.jena.sdb.sql.ResultSetJDBC;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.sql.SDBExceptionSQL;
import com.hp.hpl.jena.sdb.store.SQLBridgeFactory;
import com.hp.hpl.jena.sdb.store.SQLGenerator;
import com.hp.hpl.jena.sdb.store.StoreFormatter;
import com.hp.hpl.jena.sdb.store.StoreLoader;

public class StoreBaseIndex extends StoreBase
{
    public StoreBaseIndex(SDBConnection connection, StoreDesc desc, StoreFormatter formatter, StoreLoader loader, QueryCompilerFactory compilerF, SQLBridgeFactory sqlBridgeF, SQLGenerator sqlGenerator)
    {
        super(connection, desc, 
              formatter, loader, compilerF, sqlBridgeF, sqlGenerator,
              new TableDescTriples(),
              new TableDescQuads(),
              new TableNodesIndex()) ;
    }

	public long getSize(Node node)
	{
	    return getSize(getConnection(), getQuadTableDesc(), node) ;
	}
	
	public static long getSize(SDBConnection connection, TableDescQuads tableDescQuads, Node node)
	{ 
        
        String lex = NodeLayout2.nodeToLex(node);
        int typeId = NodeLayout2.nodeToType(node);

        String lang = "";
        String datatype = "";

        if (node.isLiteral())
        {
            lang = node.getLiteralLanguage();
            datatype = node.getLiteralDatatypeURI();
            if (datatype == null)
                datatype = "";
        }

        ResultSetJDBC rsx = null ;
        long hash = NodeLayout2.hash(lex, lang, datatype, typeId) ;
        try
        {
            rsx = connection.exec("SELECT id FROM Nodes WHERE hash = " + hash) ;
            ResultSet res = rsx.get() ;
            int id = -1 ;
            if (res.next()) 
                id = res.getInt(1) ;
            else
                // no graph, size == 0
                return 0 ;
            rsx.close();
            rsx = connection.exec("SELECT COUNT(*) FROM " + tableDescQuads.getTableName() + " WHERE g = " + id) ;
            res = rsx.get() ;
            res.next() ;
            long result = res.getLong(1) ;
            return result ;
        } catch (SQLException e)
        {
            throw new SDBExceptionSQL("Failed to get graph size", e) ;
        } finally
        {
            RS.close(rsx) ;
        }
 	}
}

/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */