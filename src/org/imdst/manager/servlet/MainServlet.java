package org.imdst.manager.servlet;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.imdst.manager.OkuyamaManagerServer;
import org.imdst.client.ImdstKeyValueClient;
import org.imdst.util.ImdstDefine;


public class MainServlet extends HttpServlet {
    private StringBuffer pageBuf = null;

    public MainServlet() {
    }

    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();
        HashMap data = new HashMap();

        ImdstKeyValueClient imdstKeyValueClient = null;
        String[] nodeRet = null;
        String msg = "&nbsp;";
        try {

            ArrayList settingList = new ArrayList();
            ArrayList dataNodeList = new ArrayList();
            ArrayList slaveDataNodeList = new ArrayList();
            HashMap setting = null;
            String[] clientRet = null;
            String dataNodeStr = null;
            String slaveDataNodeStr = null;

            String[] settingParams = new String[8];
            settingParams[0] = ImdstDefine.Prop_KeyMapNodesInfo;
            settingParams[1] = ImdstDefine.Prop_SubKeyMapNodesInfo;
            settingParams[2] = ImdstDefine.Prop_KeyMapNodesRule;
            settingParams[3] = ImdstDefine.Prop_LoadBalanceMode;
            settingParams[4] = ImdstDefine.Prop_TransactionMode;
            settingParams[5] = ImdstDefine.Prop_TransactionManagerInfo;
            settingParams[6] = ImdstDefine.Prop_MainMasterNodeInfo;
            settingParams[7] = ImdstDefine.Prop_AllMasterNodeInfo;

            // MasterNodeに接続
            imdstKeyValueClient = OkuyamaManagerServer.getClient();
            imdstKeyValueClient.nextConnect();

            // 変更が実行されている場合は更新を実行
            if (request.getParameter("execmethod") != null && 
                    ((String)request.getParameter("execmethod")).equals("modparam")) {

                if(!this.execModParam(imdstKeyValueClient, 
                                    (String)request.getParameter("updateparam"), 
                                        (String)request.getParameter((String)request.getParameter("updateparam")))) 
                                            throw new Exception("Update Param Error Param=[" + (String)request.getParameter("updateparam") + "]");
                msg = "Update Parameter Success";
            }

            for (int idx = 0; idx < settingParams.length; idx++) {

                clientRet = imdstKeyValueClient.getValue(ImdstDefine.ConfigSaveNodePrefix + settingParams[idx]);
                setting = new HashMap();
                setting.put("param", settingParams[idx]);
                setting.put("value", "&nbsp;");

                // Read Only チェック
                if (settingParams[idx].equals(ImdstDefine.Prop_MainMasterNodeInfo)) {
                    setting.put("readonly", "");
                }

                if (clientRet[0].equals("true")) {
                    if (idx == 0) {
                        if (clientRet[1] != null && !clientRet[1].equals("")) {
                            dataNodeStr = clientRet[1];
                            String[] dataNodeInfos = dataNodeStr.split(",");
                            for (int dni = 0; dni < dataNodeInfos.length; dni++) {
                                String dataNodeDt = imdstKeyValueClient.getDataNodeStatus(dataNodeInfos[dni]);
                                if (dataNodeDt == null) dataNodeDt = "";
                                HashMap dataNodeDtMap = new HashMap(2);
                                dataNodeDtMap.put("name", dataNodeInfos[dni]);
                                dataNodeDtMap.put("dt", dataNodeDt);
                                dataNodeList.add(dataNodeDtMap);
                            }
                        }
                    }

                    if (idx == 1) {
                        if (clientRet[1] != null && !clientRet[1].equals("")) {
                            slaveDataNodeStr = clientRet[1];
                            String[] slaveDataNodeInfos = slaveDataNodeStr.split(",");
                            for (int dni = 0; dni < slaveDataNodeInfos.length; dni++) {
                                String slaveDataNodeDt = imdstKeyValueClient.getDataNodeStatus(slaveDataNodeInfos[dni]);
                                if (slaveDataNodeDt == null) slaveDataNodeDt = "";
                                HashMap slaveDataNodeDtMap = new HashMap(2);
                                slaveDataNodeDtMap.put("name", slaveDataNodeInfos[dni]);
                                slaveDataNodeDtMap.put("dt", slaveDataNodeDt);
                                slaveDataNodeList.add(slaveDataNodeDtMap);
                            }
                        }
                    }

                    setting.put("value", clientRet[1]);
                }
                settingList.add(setting);
            }

            
            out.println(initPage(settingList, dataNodeList, slaveDataNodeList, msg));
            out.flush();

        } catch(Exception e) {
            out.println("<html>");
            out.println("<h1>Distributed Key Value Store Okuyama Manager Console</h1>");
            out.println("<h2>Error <h2>");
            out.println("</html>");
            out.flush();

            e.printStackTrace();
        }
    }

    private boolean execModParam (ImdstKeyValueClient imdstKeyValueClient, String updateParamKey, String updateParamVal) throws Exception {
        boolean ret = false;
        try {
            if (imdstKeyValueClient.setValue(ImdstDefine.ConfigSaveNodePrefix + updateParamKey, updateParamVal)) ret = true;
        } catch(Exception e) {
            throw e;
        }
        return ret;
        
    }

    private String initPage(ArrayList settingList, ArrayList dataNodeList, ArrayList slaveDataNodeList, String msg) {
        StringBuffer pageBuf = new StringBuffer();
        
        pageBuf.append("<html>");
        pageBuf.append("  <head>");
        pageBuf.append("    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");
        pageBuf.append("    <title>okuyama manager console</title>");
        pageBuf.append("  </head>");
        pageBuf.append("  <body>");
        pageBuf.append("    <form name='main' method='post' action'/okuyamamgr'>");
        pageBuf.append("      <input type='hidden' name='execmethod' value=''>");
        pageBuf.append("      <input type='hidden' name='updateparam' value=''>");
        pageBuf.append("      <table width=1024px align=center boder=0>");
        pageBuf.append("      <tr>  <td align=center>");
        pageBuf.append("      <h1>Distributed Key Value Store Okuyama Manager Console  </h1>");
        pageBuf.append("      <h2>Manager Display  </h2>");
        pageBuf.append("      <br />");
        pageBuf.append("      <hr />");
        pageBuf.append(msg);
        pageBuf.append("      <table style='width:760px;' border=1>");
        pageBuf.append("        <tr>");
        pageBuf.append("          <td colspan=2 align=center bgcolor='Silver' width=760px>");
        pageBuf.append("          Okuyama Setup Information");
        pageBuf.append("          </td>");
        pageBuf.append("        </tr>");

        for (int i = 0; i < settingList.size(); i++) {
            HashMap setting = (HashMap)settingList.get(i);
            pageBuf.append("  <tr>");
            pageBuf.append("    <td width=160px>");
            pageBuf.append("      <p>" + (String)setting.get("param") + "  </p>");
            pageBuf.append("    </td>");
            pageBuf.append("    <td width=600px>");
            if (setting.get("readonly") == null) {
                pageBuf.append("      <input type='text' name='" + (String)setting.get("param") + "' value='" + (String)setting.get("value") + "' size=50>");
                pageBuf.append("      &nbsp;&nbsp;<input type='submit' value='UPDATE' onclick='document.main.execmethod.value=\"modparam\";document.main.updateparam.value=\"" + (String)setting.get("param") + "\";'>");
            } else{
                pageBuf.append("      <input type='text' name='" + (String)setting.get("param") + "' value='" + (String)setting.get("value") + "' size=50 disabled>");
            }
            pageBuf.append("    </td>");
            pageBuf.append("  </tr>");
        }
        pageBuf.append("      </td>  </tr>");
        pageBuf.append("      </table>");
        pageBuf.append("      <br>");

        if (dataNodeList.size() > 0) {
            pageBuf.append("      <table style='width:760px;' border=1>");
            pageBuf.append("        <tr>");
            pageBuf.append("          <td colspan=2 align=center bgcolor='Silver' width=760px>");
            pageBuf.append("            Okuyama DataNode Information");
            pageBuf.append("          </td>");
            pageBuf.append("        </tr>");

            for (int i = 0; i < dataNodeList.size(); i++) {
                HashMap dataNode = (HashMap)dataNodeList.get(i);
                pageBuf.append("  <tr>");
                pageBuf.append("    <td width=160px>");
                pageBuf.append("      <p>" + (String)dataNode.get("name") + "  </p>");
                pageBuf.append("    </td>");
                pageBuf.append("    <td width=600px>");

                String[] nodeDtList = ((String)dataNode.get("dt")).split(";");
                for (int idx = 0; idx < nodeDtList.length; idx++)  {
                    pageBuf.append(nodeDtList[idx]);
                    pageBuf.append("  <br>");
                }

                pageBuf.append("    </td>");
                pageBuf.append("  </tr>");
            }
            pageBuf.append("      </td>  ");
            pageBuf.append("    </tr>  ");
            pageBuf.append("  </table>");
        }

        pageBuf.append("      <br>");
        if (slaveDataNodeList.size() > 0) {
            pageBuf.append("      <table style='width:760px;' border=1>");
            pageBuf.append("        <tr>");
            pageBuf.append("          <td colspan=2 align=center bgcolor='Silver' width=760px>");
            pageBuf.append("            Okuyama SlaveDataNode Information");
            pageBuf.append("          </td>");
            pageBuf.append("        </tr>");

            for (int i = 0; i < slaveDataNodeList.size(); i++) {
                HashMap slaveDataNode = (HashMap)slaveDataNodeList.get(i);
                pageBuf.append("  <tr>");
                pageBuf.append("    <td width=160px>");
                pageBuf.append("      <p>" + (String)slaveDataNode.get("name") + "  </p>");
                pageBuf.append("    </td>");
                pageBuf.append("    <td width=600px>");

                String[] nodeDtList = ((String)slaveDataNode.get("dt")).split(";");
                for (int idx = 0; idx  < nodeDtList.length; idx++)  {
                    pageBuf.append(nodeDtList[idx]);
                    pageBuf.append("  <br>");
                }

                pageBuf.append("    </td>");
                pageBuf.append("  </tr>");
            }
            pageBuf.append("      </td>  </tr>");
            pageBuf.append("      </table>");
        }
        pageBuf.append("      </table>");
        pageBuf.append("    </form>");
        pageBuf.append("  </body>");
        pageBuf.append("</html>");
        return pageBuf.toString();
    }
}
