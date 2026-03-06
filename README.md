# wizard
Library with wizard implementation

## wizard-api

This module defines the step API for the wizard.  

The core interface is `Step` with no methods.  
Interface `Step` has two descendents
- LeafStep - step without children
- HierarchicalStep - step with children of type `Step`

Interface `StepState` defines the state of a step. It contains information about the current hierarchical and leaf step.  
The main engine of the wizard is `StepManager` which manages the movement between steps and the state of the wizard.  
But the definition of the steps and their hierarchy is defined in `HierarchicalStep`.  

## Leaf step

Leaf step is a step without children. It is defined by the interface `LeafStep` which extends `Step`.  
There are two methods used during step movement `complete()`, `boolean isValid()`  
- `complete()` - is called when moving to the next step and this step is valid
- `isValid()` - is called to check if the step is valid before moving to the next step. It should return true if the step is valid and false otherwise.
Other important method is
- `T getValue()` - returns the value of the step. It could be any class

## Hierarchical step

Hierarchical step is a step with children. It is defined by the interface `HierarchicalStep` which extends `Step`.

---

## Using wizard-simple

The `wizard-simple` module provides ready-to-use implementations of the wizard API:

| Class | Description |
|---|---|
| `SimpleLeafStep<T>` | Leaf step built via a fluent builder |
| `SimpleHierarchicalStep<T>` | Hierarchical step built via a fluent builder |
| `SimpleStepState<H, L>` | Mutable holder of the current hierarchical and leaf step |
| `SimpleStepManager<T, H, L>` | Drives navigation: `next()`, `prev()`, `get()` |

### Maven dependency

```xml
<dependency>
    <groupId>cz.masci.wizard</groupId>
    <artifactId>wizard-simple</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

---

### Simple linear flow

The most basic wizard: a root group containing three consecutive leaf steps.

```
Root
 ├─ Step 1 – Personal info
 ├─ Step 2 – Address
 └─ Step 3 – Summary
```

```java
// 1. Build leaf steps (String is the value type held by each step)
var step1 = SimpleLeafStep.<String>builder()
        .name("Personal info")
        .value("alice")
        .build();

var step2 = SimpleLeafStep.<String>builder()
        .name("Address")
        .value("123 Main St")
        .build();

var step3 = SimpleLeafStep.<String>builder()
        .name("Summary")
        .value("ready")
        .build();

// 2. Build the root hierarchical step
var root = SimpleHierarchicalStep.<Void>builder()
        .addChild(step1)
        .addChild(step2)
        .addChild(step3)
        .build();

// 3. Create state and manager
var state   = new SimpleStepState<Void, String>();
var manager = new SimpleStepManager<>(state, root);

// 4. Navigate
manager.next(); // → step1  (Personal info)
manager.next(); // → step2  (Address)
manager.next(); // → step3  (Summary)
manager.next(); // → wizard finished (no more steps)

// Read the current step name and value
state.getName().ifPresent(System.out::println);  // e.g. "Address"
state.getValue().ifPresent(System.out::println); // e.g. "123 Main St"
```

> **Note:** `next()` will NOT advance if the current leaf step returns `false` from `isValid()`.

---

### Flow with a skippable step

Use `skipNextStepPredicate` / `skipPrevStepPredicate` on a `SimpleHierarchicalStep` to conditionally bypass steps during forward or backward navigation.

```
Root
 ├─ Step 1 – Basic info
 ├─ Step 2 – Optional details  ← skipped when flag is false
 └─ Step 3 – Confirmation
```

```java
// Flag that decides whether step 2 should be shown
var showDetails = new AtomicBoolean(false);

var step1 = SimpleLeafStep.<String>builder().name("Basic info").value("data").build();
var step2 = SimpleLeafStep.<String>builder().name("Optional details").value("extra").build();
var step3 = SimpleLeafStep.<String>builder().name("Confirmation").value("ok").build();

var root = SimpleHierarchicalStep.<Void>builder()
        .addChild(step1)
        .addChild(step2)
        .addChild(step3)
        // Skip step2 (index 1) when moving forward if showDetails is false
        .skipNextStepPredicate((idx, step) -> idx == 1 && !showDetails.get())
        // Skip step2 (index 1) when moving backward if showDetails is false
        .skipPrevStepPredicate((idx, step) -> idx == 1 && !showDetails.get())
        .build();

var state   = new SimpleStepState<Void, String>();
var manager = new SimpleStepManager<>(state, root);

// showDetails is false → step2 is skipped
manager.next(); // → step1
manager.next(); // → step3  (step2 was skipped)
manager.prev(); // → step1  (step2 was skipped going back)

// Enable details and restart
showDetails.set(true);
root.reset();
state.setHierarchicalStep(null);
state.setLeafStep(null);

manager.next(); // → step1
manager.next(); // → step2  (now shown)
manager.next(); // → step3
```

---

### Flow with rewind to the beginning of a sub-flow

Use `rewind()` on a `SimpleHierarchicalStep` to jump back to its **first** child step (index 0), or `reset()` to go before the first child (index -1).  
A common pattern is to trigger the rewind from inside the `doBeforeEntry` or `cancelNextStepPredicate` hooks.

```
Root
 ├─ Intro  (leaf)
 └─ Confirmation sub-flow  (hierarchical)
      ├─ Review   (leaf)
      ├─ Agree    (leaf)
      └─ Submit   (leaf)
```

```java
var intro  = SimpleLeafStep.<String>builder().name("Intro").value("welcome").build();
var review = SimpleLeafStep.<String>builder().name("Review").value("items").build();
var agree  = SimpleLeafStep.<String>builder().name("Agree").value("yes").build();
var submit = SimpleLeafStep.<String>builder().name("Submit").value("done").build();

// Flag: the user declined and wants to restart the sub-flow
var declined = new AtomicBoolean(false);

var confirmationFlow = SimpleHierarchicalStep.<String>builder()
        .addChild(review)
        .addChild(agree)
        .addChild(submit)
        .status("confirmation")
        // When entering the sub-flow from the forward direction, rewind it to the
        // first child so it always starts from "Review", even on re-entry
        .doBeforeEntry(step -> step.rewind())
        // If the user declined, cancel the forward move and stay on the current step
        .cancelNextStepPredicate((idx, step) -> {
            if (declined.get()) {
                declined.set(false); // reset the flag
                step.rewind();       // restart sub-flow from "Review"
                return true;         // cancel the move – stay put
            }
            return false;
        })
        .build();

var root = SimpleHierarchicalStep.<Void>builder()
        .addChild(intro)
        .addChild(confirmationFlow)
        .build();

var state   = new SimpleStepState<Void, String>();
var manager = new SimpleStepManager<>(state, root);

manager.next(); // → intro
manager.next(); // → review   (enters confirmationFlow, doBeforeEntry rewinds it)
manager.next(); // → agree
manager.next(); // → submit

// User declines on submit – rewind the sub-flow to "Review"
declined.set(true);
manager.next(); // cancelNextStepPredicate fires → rewinds to review, move is cancelled
                // current step remains on submit but sub-flow is rewound

manager.next(); // → review   (sub-flow restarted from beginning)
manager.next(); // → agree
manager.next(); // → submit
manager.next(); // → wizard finished
```

> **`rewind()` vs `reset()`**
> - `rewind()` – sets `currentIdx` to `0`; the first child step is the active one.
> - `reset()` – sets `currentIdx` to `-1`; the hierarchical step is back to its initial "not started" state.  
>   All nested `HierarchicalStep` children are recursively reset in both cases.
