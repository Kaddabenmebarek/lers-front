package org.research.kadda.labinventory.core.service;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.research.kadda.labinventory.core.utils.MiscUtils;
import org.springframework.stereotype.Component;

import com.actelion.research.chem.Depictor2D;
import com.actelion.research.chem.StereoMolecule;
import com.actelion.research.osiris.business.SubstanceFlagColor;
import com.actelion.research.osiris.business.SubstanceRecord;
import com.actelion.research.osiris.services.DBCompoundRegistrar;
import com.actelion.research.osiris.services.DBMolecule;
import org.research.kadda.osiris.OsirisService;
import org.research.kadda.osiris.data.SampleDto;

@Component
public class StructureService {

	private static Logger logger = LogManager.getLogger(StructureService.class);
	private static String ACT_NO = "ACT-";
	private static String IDO_NO = "IDOR-";
	
	public String drawStructure(String compoundName, boolean isTransparentImageStructure) {
		logger.info("Getting Structure for compound " + compoundName);
		boolean isNotExteRef = StringUtils.startsWith(compoundName, ACT_NO) || StringUtils.startsWith(compoundName, IDO_NO); 
		int width = 250;
		int height = 250;
		BufferedImage img;
		if(!isTransparentImageStructure) {
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}else {
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(isTransparentImageStructure) {
			g.setComposite(AlphaComposite.Clear);
			g.setColor(Color.WHITE);
			g.fillRect(0,0, width, height);
			g.setComposite(AlphaComposite.Src);
			g.setColor(Color.BLACK);
		}else {
			g.setColor(Color.WHITE);
			g.fillRect(0,0, width, height);
			g.setColor(Color.BLACK);
		}
		if(!isNotExteRef) {
			List<SampleDto> samples = OsirisService.getSamplesByExtReference(compoundName);
			compoundName = samples != null && !samples.isEmpty() ? samples.get(0).getActNo() : compoundName;
		}
		try {
			StereoMolecule m = DBMolecule.loadMoleculeFromActNo(compoundName);
			if(m != null) {
				List<SubstanceFlagColor> sfcs = new ArrayList<>();
				SubstanceRecord sr = DBCompoundRegistrar.getSubstanceInfo(compoundName);
				sfcs = SubstanceFlagColor.getSubstanceFlags(sr.flag);
				Depictor2D depictor2D = new Depictor2D(m);
				depictor2D.updateCoords(g, new Rectangle2D.Double(0, 0, width, height), Depictor2D.cModeInflateToMaxAVBL+64);
				depictor2D.paint(g);
				if(sfcs.size() > 0) {
						int x[] = new int[3];
						int y[] = new int[3];
						x[0] = width;
						y[0] = 0;
						y[1] = 0;
						x[2] = width;
						int shift = 20 / sfcs.size();
						x[1] = width - 20;
						y[2] = 20;
						for (SubstanceFlagColor sfc : sfcs) {
							g.setColor(sfc.getAwt());
							g.fillPolygon(x, y, 3);
							x[1] += shift;
							y[2] -= shift;
						}
					}
				} else {
					g.setColor(new Color(240,240,240));
					g.drawLine(0, 0, width, height);
					g.drawLine(0, height, width, 0);
				}
			try {
		        byte[] byteArray = MiscUtils.toByteArray(img, "png");
		        return Base64.encodeBase64String(byteArray);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
