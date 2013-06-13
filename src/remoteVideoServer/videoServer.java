package remoteVideoServer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.monte.screenrecorder.ScreenRecorder;

/**
 * Servlet implementation class videoServer
 */
@WebServlet("/videoServer")
public class videoServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static ScreenRecorder sr;

	/**
	 * @throws Exception
	 * @see HttpServlet#HttpServlet()
	 */
	public videoServer() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("action ->" + request.getParameter("action"));
		String action = request.getParameter("action") != null ? request
				.getParameter("action") : "";
		if (action.equalsIgnoreCase("start")) {
			System.out.println("Start");
			try {
				if (sr == null) {
					sr = Utilities.getScreenRecorder();
				}
				sr.start();
				System.out.println("video started!");

			} catch (Exception e) {

				e.printStackTrace();
			}

		} else if (action.equalsIgnoreCase("stop")) {
			System.out.println("Stop");
			if (sr != null) {
				sr.stop();
			}
		} else if (action.equalsIgnoreCase("getVideo")) {
			System.out.println("Get Video");
			if (sr != null) {
				if (sr.getCreatedMovieFiles() != null) {
					// if (!sr.getCreatedMovieFiles().isEmpty()) {
					String range = request.getHeader("range");
					if (response != null) {

						System.out.println("getting video => " + sr.getCreatedMovieFiles().get(0));
						byte[] data = Utilities
								.getBytesFromFile(sr.getCreatedMovieFiles().get(0));
						if (data != null) {
							response.setHeader("Content-Range",
									range + Integer.valueOf(data.length - 1));
							response.setHeader("Accept-Ranges", "bytes");
							response.setHeader("Content-Disposition", "attachment; filename=\"" + sr.getCreatedMovieFiles().get(0).getName() + "\"");
							response.setHeader("Etag",
									"W/\"9767057-1323779115364\"");
							byte[] content = new byte[1024];
							BufferedInputStream is = new BufferedInputStream(
									new ByteArrayInputStream(data));
							OutputStream os = response.getOutputStream();
							while (is.read(content) != -1) {
								os.write(content);
							}  
							is.close();
							os.close();
						}
					} else {
						System.out.println("response = null");
					}
				}
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("action POST->" + request.getParameter("action"));
	}

}
