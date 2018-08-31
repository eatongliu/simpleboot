package com.gpdata.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacUtil {
    private static Logger logger = LoggerFactory.getLogger(MacUtil.class);
    public static String callCmd(String[] cmd) {
        String result = "";
        String line;
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader (is);
            while ((line = br.readLine ()) != null) {
                result += line;
            }
        }
        catch(Exception e) {
            logger.error("执行命令发生错误，错误原因是：", e.getMessage());
        }
        return result;
    }

    /**
     *
     * @param cmd 第一个命令
     * @param another 第二个命令
     * @return  第二个命令的执行结果
     */
    public static String callCmd(String[] cmd,String[] another) {
        String result = "";
        String line;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            proc.waitFor(); //已经执行完第一个命令，准备执行第二个命令
            proc = rt.exec(another);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine ()) != null) {
                result += line;
            }
        }
        catch(Exception e) {
            logger.error("执行命令发生错误，错误原因是：", e.getMessage());
        }
        return result;
    }


    /**
     *
     * @param ip 目标ip,一般在局域网内
     * @param sourceString 命令处理的结果字符串
     * @param macSeparator mac分隔符号
     * @return mac地址，用上面的分隔符号表示
     */
    public static String filterMacAddress(final String ip, final String sourceString,final String macSeparator) {
        String result = "";
        String regExp = "((([0-9A-Fa-f]{1,2}" + macSeparator + "){1,5})[0-9A-Fa-f]{1,2})";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(sourceString);
        while(matcher.find()){
            result = matcher.group(1);
            logger.debug("indexof(ip):{}", sourceString.indexOf(ip));
            logger.debug("lastIndexOf(mac):{}", sourceString.lastIndexOf(matcher.group(1)));
            if(sourceString.indexOf(ip) <= sourceString.lastIndexOf(matcher.group(1))) {
                logger.debug("right mac:{}", result);
                break; //如果有多个IP,只匹配本IP对应的Mac.
            }
        }
        return result;
    }



    /**
     *
     * @param ip 目标ip
     * @return  Mac Address
     *
     */
    public static String getMacInWindows(final String ip){
        logger.debug("执行getMacInWindows()...");
        String[] cmd = {
                "cmd",
                "/c",
                "ping " + ip
        };
        String[] another = {
                "cmd",
                "/c",
                "arp -a"
        };

        String cmdResult = callCmd(cmd,another);

        return filterMacAddress(ip,cmdResult,"-");
    }

    /**
     * @param ip 目标ip
     * @return  Mac Address
     *
     */
    public static String getMacInLinux(final String ip){
        logger.debug("执行getMacInLinux()...");
        String[] cmd = {
                "/bin/sh",
                "-c",
                "ping " + ip + " -c 2"
        };
        String[] another = {
                "/bin/sh",
                "-c",
                "arp -a"
        };
        String cmdResult = callCmd(cmd,another);
        logger.debug(cmdResult);
        return filterMacAddress(ip,cmdResult,":");
    }

    /**
     * 获取MAC地址
     * @return 返回MAC地址
     */
    public static String getMacAddress(String ip) throws Exception{
        logger.debug("ip地址为：{}", ip);
        String macAddress;
        List<String> localIp = Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1");
        //如果为127.0.0.1或者0:0:0:0:0:0:0:1,则获取本地MAC地址。
        if (localIp.contains(ip)) {
            InetAddress inetAddress = InetAddress.getLocalHost();
            //貌似此方法需要JDK1.6。
            byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
            //下面代码是把mac地址拼装成String
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                //mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            //把字符串所有小写字母改为大写成为正规的mac地址并返回
            return sb.toString().trim().toUpperCase();
        }
        //获取非本地IP的MAC地址
        macAddress = getMacInWindows(ip).trim();
        if(StringUtils.isBlank(macAddress)){
            macAddress = getMacInLinux(ip).trim();
        }
        return macAddress;
    }

    /**
     * 通过HttpServletRequest返回IP地址
     * @param request HttpServletRequest
     * @return ip String
     * @throws Exception
     */
    public static String getIpAddr(HttpServletRequest request) throws Exception {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    //做个测试
    public static void main(String[] args) {
        try {
            System.out.println(getMacAddress("59.108.36.225"));
        } catch (Exception e) {

        }
    }
}
