package remoteVideoServer;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

public class Utilities {
	static String AUTOMATION_PATH = "C:\\results\\";

	public static ScreenRecorder getScreenRecorder() throws Exception {

		GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();

		ScreenRecorder screenRecorder = new ScreenRecorder(gc, new Format(
				MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
				new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey,
						ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
						CompressorNameKey,
						ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24,
						FrameRateKey, Rational.valueOf(15), QualityKey, 1.0f,
						KeyFrameIntervalKey, 15 * 60), new Format(MediaTypeKey,
						MediaType.VIDEO, EncodingKey, "black", FrameRateKey,
						Rational.valueOf(30)), null);

		return screenRecorder;
	}

	public static void moveFile(String newName, List<File> createdMovieFiles) {
		int part = 0;
		for (File file : createdMovieFiles) {
			file.renameTo(new File(AUTOMATION_PATH + newName + part + ".avi"));
			part++;
		}
	}

	public static void removeFiles(List<File> createdMovieFiles) {
		for (File file : createdMovieFiles) {
			file.delete();
		}
	}

	public static File getHomePath() {
		File folder;
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			folder = new File(new StringBuilder()
					.append(System.getProperty("user.home"))
					.append(File.separator).append("Videos").toString());
		} else {
			folder = new File(new StringBuilder()
					.append(System.getProperty("user.home"))
					.append(File.separator).append("Movies").toString());
		}
		return folder;
	}

	public static void showVideos(HttpServletRequest request,
			HttpServletResponse response) {
		try {

			String videoNames = "<html><body><p>Videos</p>";
			File folder = getHomePath();

			for (File file : folder.listFiles()) {
				if (!file.isDirectory()) {
					videoNames = videoNames
							+ "<a href='\\videoRemote?action=getVideo&video="
							+ file.getName() + "'>" + file.getName()
							+ "</a><br>";
				}
			}
			videoNames = videoNames + "</body></html>";

			System.out.println(videoNames);

			response.setContentLength(videoNames.length());
			PrintWriter out;

			out = response.getWriter();

			out.print(videoNames);
			out.close();
			out.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getVideos(HttpServletRequest request,
			HttpServletResponse response, ScreenRecorder sr) {

		String videoName = request.getParameter("video") != null ? request
				.getParameter("video") : "";

		String range = request.getHeader("range");

		byte[] data = null;
		try {
			if (videoName != "") {
				System.out.println("video ->"
						+ Utilities.getHomePath().getAbsolutePath() + "\\"
						+ videoName);
				data = getBytesFromFile(new File(Utilities.getHomePath()
						.getAbsolutePath() + "\\" + videoName));
			} else if (sr.getCreatedMovieFiles() != null) {

				System.out.println("getting video => "
						+ sr.getCreatedMovieFiles().get(0));
				data = getBytesFromFile(sr.getCreatedMovieFiles().get(0));
				videoName = sr.getCreatedMovieFiles().get(0).getName();
			}

			if (data != null) {
				response.setHeader("Content-Range",
						range + Integer.valueOf(data.length - 1));
				response.setHeader("Accept-Ranges", "bytes");
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + videoName + "\"");
				response.setHeader("Etag", "W/\"9767057-1323779115364\"");
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		try {

			InputStream is = new FileInputStream(file);
			// System.out.println("\nDEBUG: FileInputStream is " + file);
			// Get the size of the file
			long length = file.length();
			// System.out.println("DEBUG: Length of " + file + " is " + length +
			// "\n");
			/*
			 * You cannot create an array using a long type. It needs to be an
			 * int type. Before converting to an int type, check to ensure that
			 * file is not loarger than Integer.MAX_VALUE;
			 */
			if (length > Integer.MAX_VALUE) {
				System.out.println("File is too large to process");
				return null;
			}
			// Create the byte array to hold the data
			byte[] bytes = new byte[(int) length];
			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while ((offset < bytes.length)
					&& ((numRead = is
							.read(bytes, offset, bytes.length - offset)) >= 0)) {
				offset += numRead;
			}
			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ file.getName());
			}
			is.close();
			return bytes;
		} catch (Exception e) {

		}
		return null;
	}

}
