package remoteVideoServer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

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
			Utilities.getVideos(request, response, sr);
			

		} else if (action.equalsIgnoreCase("getVideoName")) {
			System.out.println("Get Video Name");
			if (sr != null) {
				if (sr.getCreatedMovieFiles() != null) {
					String videoName = sr.getCreatedMovieFiles().get(0)
							.getName();
					response.setContentLength(videoName.length());
					OutputStream out = response.getOutputStream();
					out.write(videoName.getBytes());
					out.close();
					out.flush();

				}
			}
		} else if (action.equalsIgnoreCase("getVideoFiles")) {
			System.out.println("get video Files");
			Utilities.showVideos(request, response);
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
