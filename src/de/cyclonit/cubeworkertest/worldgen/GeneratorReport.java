package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

import java.util.Arrays;

public class GeneratorReport {

	private final GeneratorStageRegistry generatorStageRegistry;

	private int[] processedStageRecent;

	private int processedRecent;

	private int skippedRecent;

	private long durationRecent;

	private int[] processedStageTotal;

	private int processedTotal;

	private int skippedTotal;

	private long durationTotal;

	private long timeStart;


	public GeneratorReport(GeneratorStageRegistry generatorStageRegistry) {
		this.generatorStageRegistry = generatorStageRegistry;
		this.processedStageRecent = new int[generatorStageRegistry.size()];
		this.processedStageTotal = new int[generatorStageRegistry.size()];
	}

	public void addProcessed(GeneratorStage generatorStage) {
		++this.processedStageRecent[generatorStage.getOrdinal()];
		++this.processedRecent;
		++this.processedStageTotal[generatorStage.getOrdinal()];
		++this.processedTotal;
	}

	public void startTimer() {
		this.timeStart = System.currentTimeMillis();
	}

	public void stopTimer() {
		long timeDiff = System.currentTimeMillis() - timeStart;
		this.durationRecent += timeDiff;
		this.durationTotal += timeDiff;
	}

	public void addSkipped() {
		++this.skippedRecent;
		++this.skippedTotal;
	}

	public void reportRecent() {
		double rate = (double) this.processedRecent * 1000f / this.durationRecent;
		rate = rate != Double.NaN ? rate : 0;

		System.out.println(String.format("Processed %d cubes in %dms (%f cubes/s). Skipped %d.", this.processedRecent, this.durationRecent, rate, this.skippedRecent));
		for (int i = 0; i < this.processedStageRecent.length; ++i) {
			System.out.println(String.format("%15s: %d", this.generatorStageRegistry.getStage(i).getName(), this.processedStageRecent[i]));
		}
	}

	public void resetRecentReport() {
		Arrays.fill(this.processedStageRecent, 0);
		this.processedRecent = 0;
		this.skippedRecent = 0;
		this.durationRecent = 0L;
	}

	public void reportTotal() {
		double rate = (double) this.processedTotal * 1000f / this.durationTotal;
		rate = rate != Double.NaN ? rate : 0;

		System.out.println(String.format("Processed %d cubes in %dms (%f cubes/s). Skipped %d.", this.processedTotal, this.durationTotal, rate, this.skippedTotal));
		for (int i = 0; i < this.processedStageTotal.length; ++i) {
			System.out.println(String.format("%15s: %d", this.generatorStageRegistry.getStage(i).getName(), this.processedStageTotal[i]));
		}
	}

}
