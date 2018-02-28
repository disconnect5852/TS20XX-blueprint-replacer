import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class RWUtils {
	private static final int BUFFER = 2048;
	private static final String FILESEP="\\";
	public static void decodeDavs(Path outputdir, Set<Path> files) throws Exception {

		for (Path current: files) {
			if (Files.isDirectory(current)) {
				DirectoryStream<Path> stream = Files.newDirectoryStream(current, "*.dav");
				for (Path subdir: stream) {
					decodeDav(outputdir, subdir);
				}
			} else {
				decodeDav(outputdir, current);
			}
		}
	}
	private static void decodeDav(Path outputdir,Path current) throws Exception {
		byte xor= 123;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(current.toFile()), 1000);
		BufferedOutputStream out;
		if (outputdir==null) {
			out= new BufferedOutputStream(new FileOutputStream(current.toString().replaceFirst(".dav", ".wav")));
		}
		else {
			out= new BufferedOutputStream(new FileOutputStream(outputdir.toAbsolutePath().toString()+System.getProperty("file.separator") +current.getFileName().toString().replaceFirst(".dav", ".wav")));
		}
		int count;
		byte[] data  = new byte[BUFFER];
		byte[] output  = new byte[BUFFER];
		while((count = in.read(data,0,BUFFER)) != -1) {
			int i=0;
			for (byte curr: data) {
				 output[i++]=(byte) (curr ^ xor);
			}
			out.write(output, 0, count);
		}
		in.close();
		out.close();
	}
	
	public static void zipsToRwp(Path outputdir, Set<Path> files) throws Exception {
		
		for (Path current: files) {
			if (Files.isDirectory(current)) {
				//ExecutorService execSvc = Executors.newFixedThreadPool( ReplacerGui.THREADCOUNT );
				DirectoryStream<Path> stream = Files.newDirectoryStream(current, "*.zip");
				for (Path subdir: stream) {
					ziptoRwp(outputdir, subdir);
				}
			} else {
				ziptoRwp(outputdir, current);
			}
		}
	}
	private static void ziptoRwp(Path outputdir,Path current) throws Exception{
		FileOutputStream out;
		if (outputdir==null) {
			out= new FileOutputStream(current.toString().replaceFirst(".zip", ".rwp"));
		}
		else {
			out= new FileOutputStream(outputdir.toAbsolutePath().toString()+System.getProperty("file.separator") +current.getFileName().toString().replaceFirst(".zip", ".rwp"));
		}
				ZipInputStream zipinputStream = new ZipInputStream(new FileInputStream(current.toFile()));
				ZipOutputStream zipOutputStream =new ZipOutputStream(out);
				byte[] header= new BigInteger("087A6970746F72777001", 16).toByteArray();
				out.write(header);
				//byte[] tmpBuf = new byte[1024];
				ZipEntry entry;
		         while((entry = zipinputStream.getNextEntry()) != null) {
		            //System.out.println("Extracting: " +entry);
		            int count;
		            byte data[] = new byte[BUFFER];
		            // write the files to the disk
		            if (!entry.isDirectory()) {
		            	
		            zipOutputStream.putNextEntry(new ZipEntry(entry.getName().toString().replace("/",FILESEP)));
		            while ((count = zipinputStream.read(data, 0, BUFFER)) != -1) {
		               zipOutputStream.write(data, 0, count);
		               
		            }
		            zipOutputStream.closeEntry();
		            }
		         }
		         zipinputStream.close();
		         zipOutputStream.close();
		         out.close();
	}
}
