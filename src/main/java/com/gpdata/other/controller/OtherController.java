package com.gpdata.other.controller;

import com.gpdata.util.MacUtil;
import com.gpdata.util.UdpGetClientMacAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by liuyutong on 2017/9/13.
 */
@Controller
public class OtherController {
    private Logger logger = LoggerFactory.getLogger(OtherController.class);

    @RequestMapping("/remotecall")
    @ResponseBody
    public Map<String, Object> remoteMethod(HttpServletRequest request) {
        ServletContext servletContext = request.getServletContext();
        String realPath = servletContext.getRealPath("/");

        Map<String, Object> map = new HashMap<>();
        logger.debug("what is up?");
        logger.info("what is up?");
        map.put("user", "lyt");
        map.put("path", realPath);
        return map;
    }

    @RequestMapping("/mac")
    @ResponseBody
    public Map<String, Object> getMac(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {

//            UdpGetClientMacAddr umac = new UdpGetClientMacAddr(MacUtil.getIpAddr(request));
//            String macAddress = umac.GetRemoteMacAddr();
            String macAddress = "";
            final UdpGetClientMacAddr umac = new UdpGetClientMacAddr(MacUtil.getIpAddr(request));
            //---长时间获取不到MAC地址则放弃
            ExecutorService exec = Executors.newFixedThreadPool(1);
            Callable<String> call = () -> umac.GetRemoteMacAddr();
            try {
                Future<String> future = exec.submit(call);
                macAddress = future.get(1000 * 3, TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
                logger.error("获取MAC地址超时");
            }
            // 关闭线程池
            exec.shutdown();

//            String ipAddr = request.getRemoteAddr();
//            String macAddress = MacUtil.getMacAddress(ipAddr);
            map.put("user", macAddress);
        } catch (Exception e) {
            logger.error("发生异常：{}", e);
        }
        return map;
    }
}