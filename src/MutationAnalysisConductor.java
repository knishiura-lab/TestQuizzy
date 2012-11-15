import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.event_detector.TimerEventDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AttributeModificationTargetAttributeMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AttributeModificationValueMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.DOMSelectionMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventCallbackMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTargetMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.EventTypeMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.RequestOnSuccessHandlerMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.RequestUrlMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.TimerEventCallbackMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.TimerEventDurationMutator;
import jp.gr.java_conf.daisy.ajax_mutator.util.Randomizer;

import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.common.collect.ImmutableSet;


public class MutationAnalysisConductor {
	public static void main(String[] args) {
		Randomizer.setValues(new double[] {0.0, 4.0, 4.0, 2.0, 11.0, 0.0, 4.0,
				5.0, 4.0, 10.0, 12.0, 3.0, 2.0, 10.0, 2.0, 6.0, 0.0, 8.0, 6.0,
				4.0, 8.0, 3.0, 2.0, 1.0, 1.0, 2.0, 3.0, 3.0, 0.0, 3.0, 1.0, 1.0,
				3.0, 4.0, 4.0, 2.0, 4.0, 3.0, 1.0, 0.0, 1.0, 2.0, 1.0, 3.0, 3.0,
				3.0, 3.0, 2.0, 0.08391814667001973, 0.2787925828092248,
				0.8553210887042564, 0.9574850508405217, 0.7124071617723093,
				0.577763112464337, 0.5474070417376862, 0.16377023629193377,
				0.8792406157117316, 0.8958456368917288, 0.26053084097478574,
				0.5976530015374931, 0.39462903573033703, 0.5227522474267948,
				0.5528921352763372, 0.1231404880972714, 0.9185851321011997,
				0.8465449218098017, 0.4261649726047688, 0.7797596794172345,
				0.5133346976358679, 0.13207506264910585, 0.9581395276010942,
				0.13585647493827002, 0.634509562807588, 0.9305875235694265,
				0.5870362893161096, 0.3921999362511893, 0.2994032972893066,
				0.6712705076794929, 0.4107738487923339, 0.41167321104395516,
				0.42057141041318435, 0.2355325479047421, 0.11447151932542987,
				0.10083175020301116, 0.5430566742954002, 0.5647875887409383,
				0.38946805576145993, 0.8191108229638654, 0.47352156800214074,
				0.6025299377679323, 0.9571377727139422, 0.7414628933187379,
				0.5395684590499711, 0.12147971159866211, 0.5607853206064781,
				0.35020667631191216, 0.46752909593338987, 0.23658049244854928,
				0.5389001735557577, 0.4818687131020791, 0.11700464637095254,
				0.6546303340904519, 0.8430524381780035, 4.0, 1.0, 3.0, 1.0,
				6.0, 5.0, 4.0, 4.0, 1.0, 3.0, 1.0, 5.0, 3.0, 4.0, 4.0, 6.0, 5.0,
				6.0, 1.0, 2.0, 4.0, 3.0, 4.0, 4.0, 6.0, 5.0, 5.0, 2.0, 2.0, 5.0,
				0.0, 4.0, 5.0, 4.0, 3.0, 3.0, 0.0, 1.0, 5.0, 6.0, 6.0, 6.0, 3.0,
				1.0, 6.0, 4.0, 3.0, 1.0, 3.0, 4.0, 4.0, 6.0, 4.0, 0.0, 0.0, 5.0,
				0.0, 3.0, 6.0, 5.0, 5.0, 0.0, 1.0, 1.0, 6.0, 3.0, 1.0, 0.0, 2.0,
				6.0, 5.0, 6.0, 5.0, 3.0, 5.0, 5.0, 1.0, 5.0, 1.0, 1.0, 2.0, 2.0,
				2.0, 0.0, 3.0, 4.0, 3.0, 2.0, 4.0, 0.0, 1.0, 2.0, 6.0, 6.0, 5.0,
				2.0, 3.0, 4.0, 6.0, 3.0, 1.0, 4.0, 5.0, 5.0, 1.0, 5.0, 3.0, 0.0,
				2.0, 0.0, 2.0, 0.0, 3.0, 1.0, 3.0, 3.0, 6.0, 3.0, 5.0, 1.0, 3.0,
				1.0, 7.0, 12.0, 2.0, 10.0, 2.0, 4.0, 4.0, 8.0, 1.0, 11.0, 10.0,
				7.0, 9.0, 6.0, 7.0});

		MutateVisitorBuilder builder = new MutateVisitorBuilder();
		builder.setAttributeModificationDetectors(
				ImmutableSet.of(new JQueryAttributeModificationDetector()));
		builder.setDomSelectionDetectors(
				ImmutableSet.of(new JQueryDOMSelectionDetector()));
		builder.setEventAttacherDetectors(
				ImmutableSet.of((EventAttacherDetector) new JQueryEventAttachmentDetector()));
		builder.setRequestDetectors(
				ImmutableSet.of(new JQueryRequestDetector()));
		builder.setTimerEventDetectors(
				ImmutableSet.of(new TimerEventDetector()));
		MutateVisitor visitor = builder.build();
		MutationTestConductor conductor = new MutationTestConductor();
		conductor.setup(LocalSettings.LOCAL_FILE_PATH, "test.target.URI", visitor);

		Mutator[] mutatorArray = new Mutator[] {
			new AttributeModificationTargetAttributeMutator(
					visitor.getAttributeModifications()),
			new RequestOnSuccessHandlerMutator(visitor.getRequests()),
			new EventTargetMutator(visitor.getEventAttachments()),
			new TimerEventCallbackMutator(visitor.getTimerEventAttachmentExpressions()),
			new RequestUrlMutator(visitor.getRequests()),
			new DOMSelectionMutator(visitor.getDomSelections()),
			new EventTypeMutator(visitor.getEventAttachments()),
			new TimerEventDurationMutator(visitor.getTimerEventAttachmentExpressions()),
			new EventCallbackMutator(visitor.getEventAttachments()),
			new AttributeModificationValueMutator(visitor.getAttributeModifications())};

		Set<Mutator> mutators = new LinkedHashSet<Mutator>(Arrays.asList(mutatorArray));
		System.out.println(visitor.MutatablesInfo());
		System.out.println("------------");
		LocalSettings.driver = new FirefoxDriver();
		conductor.conductWithJunit4(mutators, QuizzyTest.class);
		LocalSettings.driver.close();
	}
}
