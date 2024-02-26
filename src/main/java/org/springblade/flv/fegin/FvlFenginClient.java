package org.springblade.flv.fegin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.flv.factories.Converter;
import org.springblade.flv.factories.ConverterFactories;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName FvlFengin
 * @Author qiaoyc
 * @Date 2023/11/14 18:49
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class FvlFenginClient  {
//	public class FvlFenginClient implements IFlvFegin {

	private final ConcurrentHashMap<String, Converter> converters = new ConcurrentHashMap<>();

	public void rtspToflv(String rtspUrl, HttpServletRequest request, HttpServletResponse response) {
		log.info("request rtspUrl = " + rtspUrl);
		String key = md5(rtspUrl);
		AsyncContext async = request.startAsync();
		async.setTimeout(0);
		if (converters.containsKey(key)) {
			Converter c = converters.get(key);
			try {
				c.addOutputStreamEntity(key, async);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new IllegalArgumentException(e.getMessage());
			}
		} else {
			List<AsyncContext> outs = new ArrayList<>();
			outs.add(async);
			ConverterFactories c = new ConverterFactories(rtspUrl, key, converters, outs);
			c.start();
			converters.put(key, c);
		}
		response.setContentType("video/x-flv");
		response.setHeader("Connection", "keep-alive");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			response.flushBuffer();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	public String md5(String plainText) {
		StringBuilder buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte[] b = md.digest();
			int i;
			buf = new StringBuilder("");
			for (byte value : b) {
				i = value;
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		}
		return buf.toString();
	}
}
